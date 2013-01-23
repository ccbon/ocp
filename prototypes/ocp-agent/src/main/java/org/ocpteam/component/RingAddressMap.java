package org.ocpteam.component;

import java.io.EOFException;
import java.io.Serializable;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Queue;

import org.ocpteam.entity.Response;
import org.ocpteam.exception.NotAvailableContactException;
import org.ocpteam.interfaces.IAddressMap;
import org.ocpteam.interfaces.IDataStore;
import org.ocpteam.interfaces.INodeMap;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.LOG;
import org.ocpteam.serializable.Address;
import org.ocpteam.serializable.Contact;
import org.ocpteam.serializable.EOMObject;
import org.ocpteam.serializable.InputFlow;
import org.ocpteam.serializable.InputMessage;
import org.ocpteam.serializable.Node;

public class RingAddressMap extends DSContainer<AddressDataSource> implements
		IAddressMap {

	private RingNodeMap ringNodeMap;
	private IDataStore localMap;

	@Override
	public byte[] get(Address address) throws Exception {
		byte[] value = null;
		if (ringNodeMap.isResponsible(address)) {
			value = localMap.get(address);
			if (value != null) {
				return value;
			}
		}
		// not found locally so look at every ring.
		for (int i = 0; i < ringNodeMap.getRingNbr(); i++) {
			value = get(i, address);
			if (value != null) {
				break;
			}
		}
		return value;
	}

	public byte[] get(int ring, Address address) throws Exception {
		NodeMap nodeMap = ringNodeMap.getNodeMaps()[ring];
		if (nodeMap.isEmpty()) {
			return null;
		}
		if (nodeMap.isResponsible(address)) {
			return localMap.get(address);
		}
		Queue<Contact> contactQueue = nodeMap.getContactQueue(address);
		RingMapModule m = ds().getComponent(RingMapModule.class);
		Response r = ds().client.requestByPriority(contactQueue,
				new InputMessage(m.getOnRing(), ring, address));
		return (byte[]) r.getObject();
	}

	@Override
	public void put(Address address, byte[] value) throws Exception {
		// Strategy: foreach ring put the <address, value>
		for (int i = 0; i < ringNodeMap.getRingNbr(); i++) {
			put(i, address, value);
		}
	}

	public void put(int ring, Address address, byte[] value) throws Exception {
		NodeMap nodeMap = ringNodeMap.getNodeMaps()[ring];
		if (nodeMap.isEmpty()) {
			return;
		}
		if (nodeMap.isResponsible(address)) {
			localMap.put(address, value);
			return;
		}
		Queue<Contact> contactQueue = nodeMap.getContactQueue(address);
		RingMapModule m = ds().getComponent(RingMapModule.class);
		ds().client.requestByPriority(contactQueue,
				new InputMessage(m.putOnRing(), ring, address, value));
	}

	@Override
	public void remove(Address address) throws Exception {
		// Strategy: foreach ring remove the <address>
		for (int i = 0; i < ringNodeMap.getRingNbr(); i++) {
			remove(i, address);
		}
	}

	public void remove(int ring, Address address) throws Exception {
		// Strategy: foreach nodeMap remove the address
		NodeMap nodeMap = ringNodeMap.getNodeMaps()[ring];
		if (nodeMap.isEmpty()) {
			return;
		}

		if (nodeMap.isResponsible(address)) {
			localMap.remove(address);
			return;
		}
		Queue<Contact> contactQueue = nodeMap.getContactQueue(address);
		RingMapModule m = ds().getComponent(RingMapModule.class);
		ds().client.requestByPriority(contactQueue,
				new InputMessage(m.removeOnRing(), ring, address));

	}

	@Override
	public IDataStore getLocalMap() {
		return localMap;
	}

	@Override
	public void setLocalMap(IDataStore localMap) {
		this.localMap = localMap;

	}

	@Override
	public void setNodeMap(INodeMap nodeMap) {
		this.ringNodeMap = (RingNodeMap) nodeMap;
	}

	@Override
	public void onNodeArrival() throws Exception {
		// strategy: copy the <address, value> from the predecessor which have
		// address >= node_id
		Contact predecessor = ringNodeMap.getPredecessor(ds().getNode());
		if (predecessor.isMyself()) {
			if (ds().isFirstAgent()) {
				LOG.debug("first agent: ds=" + ds().getName());
				return;
			} else {
				ds().client.getExecutor().execute(new Runnable() {
					
					@Override
					public void run() {
						try {
							copyRing();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				
				return;
			}
		}
		MapModule m = ds().getComponent(MapModule.class);
		InputFlow message = new InputFlow(m.transferSubMap(), ds().getNode()
				.getNodeId());
		Socket socket = null;
		try {

			socket = ds().contactMap.getTcpClient(predecessor).borrowSocket(
					message);
			while (true) {
				Serializable serializable = ds().protocol.getStreamSerializer()
						.readObject(socket);
				if (serializable instanceof EOMObject) {
					break;
				}
				Address address = (Address) serializable;
				byte[] value = (byte[]) ds().protocol.getStreamSerializer()
						.readObject(socket);
				getLocalMap().put(address, value);
			}
			ds().contactMap.getTcpClient(predecessor).returnSocket(socket);
			socket = null;
		} catch (SocketException e) {
		} catch (EOFException e) {
		} catch (SocketTimeoutException e) {
		} catch (NotAvailableContactException e) {
		} finally {
			if (socket != null) {
				socket.close();
				socket = null;
			}
		}

	}

	private void copyRing() throws Exception {
		// copy all data from an existing ring to this new ring.
		// find an existing ring (in the contact map, take a contact
		// with a ring different from our ring.
		Contact firstContact = ds().contactMap.getOtherContacts()[0];
		Contact contact = firstContact;
		while (true) {
			IDataStore map = ds().localMap(firstContact);
			getLocalMap().putAll(map);
			contact = ds().nodeMap.getSuccessor(contact.getNode());
			if (contact == firstContact) {
				LOG.debug("break copyRing");
				break;
			}
		}
	}

	@Override
	public void onNodeNiceDeparture() throws Exception {
		// Strategy: take all local map content and send it to the predecessor.
		Contact predecessor = ringNodeMap.getPredecessor(ds().getNode());
		if (predecessor.isMyself()) {
			// it means I am the last agent.
			// the ring is lost...
			return;
		}
		MapModule m = ds().getComponent(MapModule.class);
		InputFlow message = new InputFlow(m.setMap());
		Socket socket = null;
		try {

			socket = ds().contactMap.getTcpClient(predecessor).borrowSocket(
					message);
			for (Address address : getLocalMap().keySet()) {
				ds().protocol.getStreamSerializer().writeObject(socket, address);
				ds().protocol.getStreamSerializer().writeObject(socket,
						getLocalMap().get(address));
				// read an acknowledgement for avoiding to sent to much on the
				// stream.
				ds().protocol.getStreamSerializer().readObject(socket);
			}
			ds().protocol.getStreamSerializer().writeEOM(socket);
			ds().contactMap.getTcpClient(predecessor).returnSocket(socket);
			socket = null;
		} catch (SocketException e) {
		} catch (EOFException e) {
		} catch (SocketTimeoutException e) {
		} catch (NotAvailableContactException e) {
		} finally {
			if (socket != null) {
				socket.close();
				socket = null;
			}
		}

	}

	@Override
	public void networkPicture() throws Exception {
		// list all contact including myself.
		for (NodeMap nodeMap : ringNodeMap.getNodeMaps()) {
			for (Contact c : nodeMap.getNodeMap().values()) {
				JLG.println("Contact: " + c);
				Node n = c.getNode();
				JLG.println("  Node: " + n);
				IDataStore localMap = ds().localMap(c);
				JLG.println("  Map: " + localMap);
			}
		}

	}

}
