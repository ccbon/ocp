package org.ocpteamx.protocol.dht2;

import java.io.EOFException;
import java.io.Serializable;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.ocpteam.component.DSPDataSource;
import org.ocpteam.component.DataStore;
import org.ocpteam.component.MessageDigest;
import org.ocpteam.component.NodeMap;
import org.ocpteam.component.Random;
import org.ocpteam.component.RingNodeMap;
import org.ocpteam.exception.NotAvailableContactException;
import org.ocpteam.interfaces.IDataModel;
import org.ocpteam.interfaces.IDataStore;
import org.ocpteam.interfaces.INodeMap;
import org.ocpteam.misc.Id;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.LOG;
import org.ocpteam.serializable.Address;
import org.ocpteam.serializable.Contact;
import org.ocpteam.serializable.EOMObject;
import org.ocpteam.serializable.InputFlow;
import org.ocpteam.serializable.InputMessage;
import org.ocpteam.serializable.Node;

/**
 * DHT2 is a distributed hashtable, with ring redundancy (The DHT topology is a
 * ring duplicated N times)
 * 
 * Strategies:
 * 
 * - Node Arrival: node chosen randomly and ring chosen as well randomly. data
 * transferred from ring predecessor.
 * 
 * - Node Rude Detachment: Data contained in the node is retrieved on another
 * existing ring and replaced on the detached node ring.
 * 
 * Potentials issues: - Only 1 ring...
 * 
 */
public class DHT2DataSource extends DSPDataSource {

	private IDataStore map;
	public DHT2DataModel dm;
	public RingNodeMap ringNodeMap;
	private MessageDigest md;
	private Random random;

	public DHT2DataSource() throws Exception {
		super();
		addComponent(INodeMap.class, new RingNodeMap());
		addComponent(IDataModel.class, new DHT2DataModel());
		addComponent(DHT2Module.class);
		addComponent(MessageDigest.class);
		addComponent(Random.class);
		addComponent(IDataStore.class, new DataStore());
	}

	@Override
	public void init() throws Exception {
		super.init();
		map = getComponent(IDataStore.class);
		dm = (DHT2DataModel) getComponent(IDataModel.class);
		ringNodeMap = (RingNodeMap) getComponent(INodeMap.class);
		md = getComponent(MessageDigest.class);
		random = getComponent(Random.class);
	}

	@Override
	public String getProtocolName() {
		return "DHT2";
	}

	@Override
	protected void askForNode() throws Exception {
		super.askForNode();
		setNode(new Node(new Id(md.hash(random.generate())),
				ringNodeMap.getLessPopulatedRing()));
	}

	@Override
	protected void onNodeArrival() throws Exception {
		super.onNodeArrival();
		Contact predecessor = ringNodeMap.getPredecessor(getNode());
		if (predecessor.isMyself()) {
			if (isFirstAgent()) {
				LOG.info("first agent: ds=" + getName());
				return;
			}
			// it means I am the first agent on my ring.
			// look at the other ring and copy all their content.
			copyRingContent();
		}
		DHT2Module m = getComponent(DHT2Module.class);
		InputFlow message = new InputFlow(m.transferSubMap(), getNode()
				.getNodeId());
		Socket socket = null;
		try {

			socket = contactMap.getTcpClient(predecessor).borrowSocket(message);
			while (true) {
				Serializable serializable = protocol.getStreamSerializer()
						.readObject(socket);
				if (serializable instanceof EOMObject) {
					break;
				}
				String key = (String) serializable;
				String value = (String) protocol.getStreamSerializer()
						.readObject(socket);
				store(key, value);
			}
			contactMap.getTcpClient(predecessor).returnSocket(socket);
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

	private void copyRingContent() throws Exception {
		LOG.info("start to copy ring content (ds=" + getName() + " ring="
				+ getNode().getRing() + ")");
		for (NodeMap nodeMap : ringNodeMap.getNodeMaps()) {
			for (Contact c : nodeMap.getNodeMap().values()) {
				if (c.isMyself()) {
					continue;
				}
				Map<String, String> localMap = dm.localMap(c);
				for (String key : localMap.keySet()) {
					map.put(new Address(key.getBytes()), localMap.get(key)
							.getBytes());
				}
			}
		}
	}

	@Override
	protected void onNodeNiceDeparture() throws Exception {
		super.onNodeNiceDeparture();
		// Strategy: take all local map content and send it to the predecessor.
		Contact predecessor = ringNodeMap.getPredecessor(getNode());
		if (predecessor.isMyself()) {
			// it means I am the last agent.
			// the ring is lost...
			return;
		}
		DHT2Module m = getComponent(DHT2Module.class);
		InputFlow message = new InputFlow(m.setMap());
		Socket socket = null;
		try {

			socket = contactMap.getTcpClient(predecessor).borrowSocket(message);
			for (Address a : map.keySet()) {
				String key = new String(a.getBytes());
				protocol.getStreamSerializer().writeObject(socket, key);
				protocol.getStreamSerializer().writeObject(socket, map.get(a));
				// read an acknowledgement for avoiding to sent to much on the
				// stream.
				Serializable serializable = protocol.getStreamSerializer()
						.readObject(socket);
				if (serializable instanceof EOMObject) {
					break;
				}
			}
			protocol.getStreamSerializer().writeEOM(socket);
			contactMap.getTcpClient(predecessor).returnSocket(socket);
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

	public Address getAddress(String key) throws Exception {
		return new Address(md.hash(key.getBytes()));
	}

	public void store(String key, String value) throws Exception {
		LOG.info("local store: " + key + "->" + value);
		Address a = new Address(key.getBytes());
		map.put(a, value.getBytes());
	}

	public String retrieve(String key) throws Exception {
		LOG.info("local retrieve: " + key);
		Address a = new Address(key.getBytes());
		return new String(map.get(a));
	}

	public void destroy(String key) throws Exception {
		Address a = new Address(key.getBytes());
		map.remove(a);
	}

	public Set<String> keySet() throws Exception {
		Set<Address> set = map.keySet();
		HashSet<String> result = new HashSet<String>();
		for (Address a : set) {
			result.add(new String(a.getBytes()));
		}
		return result;
	}

	public void networkPicture() throws Exception {
		// list all contact including myself.
		for (NodeMap nodeMap : ringNodeMap.getNodeMaps()) {
			for (Contact c : nodeMap.getNodeMap().values()) {
				JLG.println("Contact: " + c);
				Node n = c.getNode();
				JLG.println("  Node: " + n);
				Map<String, String> localMap = dm.localMap(c);
				JLG.println("  Map: " + localMap);
				for (String key : localMap.keySet()) {
					JLG.println("address(" + key + ")=" + getAddress(key));
				}
			}
		}
	}

	public Map<String, String> getMap() throws Exception {
		HashMap<String, String> result = new HashMap<String, String>();
		for (Address a : map.keySet()) {
			result.put(new String(a.getBytes()), new String(map.get(a)));
		}
		return result;
	}

	public synchronized void disconnectHard() throws Exception {
		super.disconnectHard();
		map.clear();
	}

	@Override
	public void onDetach(Contact contact) {
		// data maintained previousely by contact must be maintained on the
		// network.

		try {
			Contact successor = ringNodeMap.getSuccessor(contact.getNode());
			Contact predecessor = ringNodeMap.getPredecessor(contact.getNode());
			DHT2Module m = getComponent(DHT2Module.class);
			InputMessage message = new InputMessage(m.restore(), contact
					.getNode().getNodeId(), successor.getNode().getNodeId());
			client.send(predecessor, message);

		} catch (Exception e) {

		}
	}

	public void restore(Id startNodeId, Id endNodeId) throws Exception {
		int r = getNode().getRing();
		int backupRing = 0;
		if (r == 0) {
			backupRing = 1;
		}
		for (Contact c : ringNodeMap.getNodeMaps()[backupRing].getNodeMap()
				.values()) {
			DHT2Module m = getComponent(DHT2Module.class);
			InputFlow message = new InputFlow(m.getLocalMap());
			Socket socket = null;
			try {

				socket = contactMap.getTcpClient(c).borrowSocket(message);
				while (true) {
					Serializable serializable = protocol.getStreamSerializer()
							.readObject(socket);
					if (serializable instanceof EOMObject) {
						break;
					}
					String key = (String) serializable;
					String value = (String) protocol.getStreamSerializer()
							.readObject(socket);
					Id address = getAddress(key);
					if ((address.compareTo(startNodeId) > 0)
							&& (address.compareTo(endNodeId) < 0)) {
						store(key, value);
					}
				}
				contactMap.getTcpClient(c).returnSocket(socket);
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
	}

}
