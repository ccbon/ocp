package org.ocpteam.protocol.dht3;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.Serializable;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.ocpteam.component.DSPDataSource;
import org.ocpteam.component.NodeMap;
import org.ocpteam.component.RingNodeMap;
import org.ocpteam.entity.Contact;
import org.ocpteam.entity.Context;
import org.ocpteam.entity.EOMObject;
import org.ocpteam.entity.InputFlow;
import org.ocpteam.entity.InputMessage;
import org.ocpteam.entity.Node;
import org.ocpteam.exception.NotAvailableContactException;
import org.ocpteam.interfaces.IDataModel;
import org.ocpteam.interfaces.INodeMap;
import org.ocpteam.misc.Id;
import org.ocpteam.misc.JLG;

/**
 * DHT3 is a distributed hashtable based on DHT2 with following added:
 * 
 * Strategies:
 * 
 * keys are in a hash set and reflect their content: key=hash(value). key 0 is
 * special: its value is a list of all stored keys. This allows to retrieve all
 * keys.
 * 
 * 
 * 
 */
public class DHT3DataSource extends DSPDataSource {

	private Map<String, String> map;
	public DHT3DataModel dm;
	private MessageDigest md;
	public RingNodeMap ringNodeMap;

	public DHT3DataSource() throws Exception {
		super();
		addComponent(INodeMap.class, new RingNodeMap());
		addComponent(IDataModel.class, new DHT3DataModel());
		addComponent(DHT3Module.class);
	}

	@Override
	public void init() throws Exception {
		super.init();
		map = Collections.synchronizedMap(new HashMap<String, String>());
		dm = (DHT3DataModel) getComponent(IDataModel.class);
		ringNodeMap = (RingNodeMap) getComponent(INodeMap.class);
	}

	@Override
	public String getProtocolName() {
		return "DHT1";
	}

	@Override
	public synchronized void connect() throws Exception {
		JLG.debug("connect " + getName());
		super.connect();
		Context c = new Context(dm);
		setContext(c);
	}

	@Override
	protected void readNetworkConfig() throws Exception {
		JLG.debug("readNetworkConfig " + getName());
		super.readNetworkConfig();
		md = MessageDigest.getInstance(network.getProperty("hash", "SHA-1"));
		int ringNbr = Integer.parseInt(network.getProperty("ringNbr", "3"));
		ringNodeMap.setRingNbr(ringNbr);
	}

	@Override
	protected void askForNode() throws Exception {
		super.askForNode();
		setNode(new Node(hash(random()), ringNodeMap.getLessPopulatedRing()));
	}

	@Override
	protected void onNodeArrival() throws Exception {
		super.onNodeArrival();
		Contact predecessor = ringNodeMap.getPredecessor(getNode());
		if (predecessor.isMyself()) {
			if (agent.isFirstAgent()) {
				JLG.debug("first agent: ds=" + getName());
				return;
			}
			// it means I am the first agent on my ring.
			// look at the other ring and copy all their content.
			copyRingContent();
		}
		DHT3Module m = getComponent(DHT3Module.class);
		InputFlow message = new InputFlow(m.transferSubMap(), getNode()
				.getNodeId());
		Socket socket = null;
		try {

			socket = contactMap.getTcpClient(predecessor).borrowSocket(message);
			DataInputStream in = new DataInputStream(socket.getInputStream());
			while (true) {
				Serializable serializable = protocol.getStreamSerializer()
						.readObject(in);
				if (serializable instanceof EOMObject) {
					break;
				}
				String key = (String) serializable;
				String value = (String) protocol.getStreamSerializer()
						.readObject(in);
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
		JLG.debug("start to copy ring content (ds=" + getName() + " ring="
				+ getNode().getRing() + ")");
		for (NodeMap nodeMap : ringNodeMap.getNodeMaps()) {
			for (Contact c : nodeMap.getNodeMap().values()) {
				if (c.isMyself()) {
					continue;
				}
				Map<String, String> localMap = dm.localMap(c);
				map.putAll(localMap);
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
		DHT3Module m = getComponent(DHT3Module.class);
		InputFlow message = new InputFlow(m.setMap());
		Socket socket = null;
		try {

			socket = contactMap.getTcpClient(predecessor).borrowSocket(message);
			DataOutputStream out = new DataOutputStream(
					socket.getOutputStream());
			DataInputStream in = new DataInputStream(socket.getInputStream());
			for (String key : map.keySet()) {
				protocol.getStreamSerializer().writeObject(out, key);
				protocol.getStreamSerializer().writeObject(out, map.get(key));
				// read an acknowledgement for avoiding to sent to much on the
				// stream.
				Serializable serializable = protocol.getStreamSerializer()
						.readObject(in);
				if (serializable instanceof EOMObject) {
					break;
				}
			}
			protocol.getStreamSerializer().writeEOM(out);
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

	private byte[] random() {
		SecureRandom random = new SecureRandom();
		byte bytes[] = new byte[20];
		random.nextBytes(bytes);
		return bytes;
	}

	public Id hash(byte[] input) throws Exception {
		return new Id(md.digest(input));
	}

	public Id getAddress(String key) throws Exception {
		return hash(key.getBytes());
	}

	public void store(String key, String value) {
		JLG.debug("local store: " + key + "->" + value);
		map.put(key, value);
	}

	public String retrieve(String key) {
		JLG.debug("local retrieve: " + key);
		return map.get(key);
	}

	public void destroy(String key) {
		map.remove(key);
	}

	public Set<String> keySet() {
		return map.keySet();
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

	public Map<String, String> getMap() {
		return map;
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
			DHT3Module m = getComponent(DHT3Module.class);
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
			DHT3Module m = getComponent(DHT3Module.class);
			InputFlow message = new InputFlow(m.getLocalMap());
			Socket socket = null;
			try {

				socket = contactMap.getTcpClient(c).borrowSocket(message);
				DataInputStream in = new DataInputStream(
						socket.getInputStream());
				while (true) {
					Serializable serializable = protocol.getStreamSerializer()
							.readObject(in);
					if (serializable instanceof EOMObject) {
						break;
					}
					String key = (String) serializable;
					String value = (String) protocol.getStreamSerializer()
							.readObject(in);
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
