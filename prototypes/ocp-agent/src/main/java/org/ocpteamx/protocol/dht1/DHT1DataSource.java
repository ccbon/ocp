package org.ocpteamx.protocol.dht1;

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
import org.ocpteam.exception.NotAvailableContactException;
import org.ocpteam.interfaces.IDataModel;
import org.ocpteam.interfaces.INodeMap;
import org.ocpteam.misc.Id;
import org.ocpteam.misc.JLG;
import org.ocpteam.serializable.Contact;
import org.ocpteam.serializable.EOMObject;
import org.ocpteam.serializable.InputFlow;
import org.ocpteam.serializable.Node;

/**
 * DHT is a distributed hashtable, with no redundancy and no node detachment
 * management.
 * 
 * Strategies:
 * 
 * - Each agent is responsible for a specific territory specified by a nodeId
 * The responsibility is from node_id to succ(node_id).
 * 
 * - During node arrival, the new node take a part of responsibility of its
 * predecessor. Map content is transferred.
 * 
 * - During node nice departure, the node send all its content to its
 * predecessor.
 * 
 * - node_id is chosen in a random way.
 * 
 * Potentials issues: - Loss of data when an agent disappears or disconnects.
 * 
 */
public class DHT1DataSource extends DSPDataSource {

	private Map<String, String> map;
	private MessageDigest md;
	public NodeMap nodeMap;

	public DHT1DataSource() throws Exception {
		super();
		addComponent(INodeMap.class, new NodeMap());
		addComponent(IDataModel.class, new DHT1DataModel());
		addComponent(DHT1Module.class);
	}

	@Override
	public void init() throws Exception {
		super.init();
		map = Collections.synchronizedMap(new HashMap<String, String>());
		nodeMap = (NodeMap) getComponent(INodeMap.class);
	}

	@Override
	public String getProtocolName() {
		return "DHT1";
	}

	@Override
	protected void readNetworkConfig() throws Exception {
		super.readNetworkConfig();
		md = MessageDigest.getInstance(network.getProperty("hash", "SHA-1"));
	}

	@Override
	protected void askForNode() throws Exception {
		super.askForNode();
		setNode(new Node(hash(random())));
	}

	@Override
	protected void onNodeArrival() throws Exception {
		super.onNodeArrival();
		Contact predecessor = nodeMap.getPredecessor(getNode());
		if (predecessor.isMyself()) {
			// it means I am the last agent or the first agent.
			return;
		}
		DHT1Module m = getComponent(DHT1Module.class);
		InputFlow message = new InputFlow(m.subMap(), getNode().getNodeId());
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

	@Override
	protected void onNodeNiceDeparture() throws Exception {
		super.onNodeNiceDeparture();
		// Strategy: take all local map content and send it to the predecessor.
		Contact predecessor = nodeMap.getPredecessor(getNode());
		if (predecessor.isMyself()) {
			// it means I am the last agent or the first agent.
			return;
		}
		DHT1Module m = getComponent(DHT1Module.class);
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
}
