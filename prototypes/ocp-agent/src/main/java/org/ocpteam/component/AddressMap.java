package org.ocpteam.component;

import java.io.EOFException;
import java.io.Serializable;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.Queue;

import org.ocpteam.entity.Response;
import org.ocpteam.exception.NotAvailableContactException;
import org.ocpteam.interfaces.IAddressMap;
import org.ocpteam.interfaces.INodeMap;
import org.ocpteam.misc.JLG;
import org.ocpteam.serializable.Address;
import org.ocpteam.serializable.Contact;
import org.ocpteam.serializable.EOMObject;
import org.ocpteam.serializable.InputFlow;
import org.ocpteam.serializable.InputMessage;
import org.ocpteam.serializable.Node;

public class AddressMap extends DSContainer<AddressDataSource> implements IAddressMap {

	private NodeMap nodeMap;
	private Map<Address, byte[]> localMap;
	
	@Override
	public void setLocalMap(Map<Address, byte[]> localMap) {
		this.localMap = localMap;
	}

	@Override
	public byte[] get(Address address) throws Exception {
		if (nodeMap.isResponsible(address)) {
			return localMap.get(address);
		}
		Queue<Contact> contactQueue = nodeMap.getContactQueue(address);
		MapModule m = ds().getComponent(MapModule.class);
		Response r = ds().client.requestByPriority(contactQueue, new InputMessage(m.get(), address));
		return (byte[]) r.getObject();
	}

	@Override
	public void put(Address address, byte[] value) throws Exception {
		if (nodeMap.isResponsible(address)) {
			localMap.put(address, value);
			return;
		}
		Queue<Contact> contactQueue = nodeMap.getContactQueue(address);
		MapModule m = ds().getComponent(MapModule.class);
		ds().client.requestByPriority(contactQueue, new InputMessage(m.put(), address, value));
	}

	@Override
	public void remove(Address address) throws Exception {
		localMap.remove(address);
		if (nodeMap.isResponsible(address)) {
			return;
		}
		Queue<Contact> contactQueue = nodeMap.getContactQueue(address);
		MapModule m = ds().getComponent(MapModule.class);
		ds().client.requestByPriority(contactQueue, new InputMessage(m.remove(), address));
	}

	@Override
	public Map<Address, byte[]> getLocalMap() {
		return localMap;
	}

	@Override
	public void setNodeMap(INodeMap nodeMap) {
		this.nodeMap = (NodeMap) nodeMap;
	}

	@Override
	public void onNodeArrival() throws Exception {
		Contact predecessor = nodeMap.getPredecessor(ds().getNode());
		if (predecessor.isMyself()) {
			if (ds().agent.isFirstAgent()) {
				JLG.debug("first agent: ds=" + ds().getName());
				return;
			}
		}
		MapModule m = ds().getComponent(MapModule.class);
		InputFlow message = new InputFlow(m.transferSubMap(), ds().getNode()
				.getNodeId());
		Socket socket = null;
		try {

			socket = ds().contactMap.getTcpClient(predecessor).borrowSocket(message);
			while (true) {
				Serializable serializable = ds().protocol.getStreamSerializer()
						.readObject(socket);
				if (serializable instanceof EOMObject) {
					break;
				}
				Address address = (Address) serializable;
				byte[] value = (byte[]) ds().protocol.getStreamSerializer()
						.readObject(socket);
				localMap.put(address, value);
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

	@Override
	public void onNodeNiceDeparture() throws Exception {
		// Strategy: take all local map content and send it to the predecessor.
		Contact predecessor = nodeMap.getPredecessor(ds().getNode());
		if (predecessor.isMyself()) {
			// it means I am the last agent.
			// the ring is lost...
			return;
		}
		MapModule m = getComponent(MapModule.class);
		InputFlow message = new InputFlow(m.setMap());
		Socket socket = null;
		try {
			socket = ds().contactMap.getTcpClient(predecessor).borrowSocket(message);
			for (Address address : localMap.keySet()) {
				ds().protocol.getStreamSerializer().writeObject(socket, address);
				ds().protocol.getStreamSerializer().writeObject(socket,
						localMap.get(address));
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
		for (Contact c : nodeMap.getNodeMap().values()) {
			JLG.println("Contact: " + c);
			Node n = c.getNode();
			JLG.println("  Node: " + n);
			Map<Address, byte[]> localMap = ds().localMap(c);
			JLG.println("  Map: " + localMap);
		}
		
	}


}
