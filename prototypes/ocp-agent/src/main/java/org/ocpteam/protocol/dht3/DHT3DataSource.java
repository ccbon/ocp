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

import org.ocpteam.component.AddressMap;
import org.ocpteam.component.DSPDataSource;
import org.ocpteam.component.MapModule;
import org.ocpteam.component.NodeMap;
import org.ocpteam.entity.Address;
import org.ocpteam.entity.Contact;
import org.ocpteam.entity.Context;
import org.ocpteam.entity.EOMObject;
import org.ocpteam.entity.InputFlow;
import org.ocpteam.entity.Node;
import org.ocpteam.exception.NotAvailableContactException;
import org.ocpteam.interfaces.IAddressMap;
import org.ocpteam.interfaces.IDataModel;
import org.ocpteam.interfaces.INodeMap;
import org.ocpteam.misc.Id;
import org.ocpteam.misc.JLG;

/**
 * DHT3 is a distributed hashtable based on DHT1. The storage is now the
 * IAddressMap component.
 * 
 * Strategies:
 * 
 * 
 */
public class DHT3DataSource extends DSPDataSource {

	public AddressMap map;
	public DHT3DataModel dm;
	private MessageDigest md;
	public NodeMap nodeMap;

	public DHT3DataSource() throws Exception {
		super();
		addComponent(INodeMap.class, new NodeMap());
		addComponent(IAddressMap.class, new AddressMap());
		addComponent(IDataModel.class, new DHT3DataModel());
		addComponent(MapModule.class);
	}

	@Override
	public void init() throws Exception {
		super.init();
		dm = (DHT3DataModel) getComponent(IDataModel.class);
		nodeMap = (NodeMap) getComponent(INodeMap.class);
		map = (AddressMap) getComponent(IAddressMap.class);
		map.setNodeMap(nodeMap);
		map.setLocalMap(Collections
				.synchronizedMap(new HashMap<Address, byte[]>()));
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
			if (agent.isFirstAgent()) {
				JLG.debug("first agent: ds=" + getName());
				return;
			}
		}
		MapModule m = getComponent(MapModule.class);
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
				Address address = (Address) serializable;
				byte[] value = (byte[]) protocol.getStreamSerializer()
						.readObject(in);
				map.put(address, value);
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
			// it means I am the last agent.
			// the ring is lost...
			return;
		}
		MapModule m = getComponent(MapModule.class);
		InputFlow message = new InputFlow(m.setMap());
		Socket socket = null;
		try {

			socket = contactMap.getTcpClient(predecessor).borrowSocket(message);
			DataOutputStream out = new DataOutputStream(
					socket.getOutputStream());
			DataInputStream in = new DataInputStream(socket.getInputStream());
			for (Address address : map.getLocalMap().keySet()) {
				protocol.getStreamSerializer().writeObject(out, address);
				protocol.getStreamSerializer().writeObject(out,
						map.getLocalMap().get(address));
				// read an acknowledgement for avoiding to sent to much on the
				// stream.
				protocol.getStreamSerializer().readObject(in);
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

	public Address getAddress(String key) throws Exception {
		return new Address(hash(key.getBytes()));
	}

	public void networkPicture() throws Exception {
		// list all contact including myself.
		for (Contact c : nodeMap.getNodeMap().values()) {
			JLG.println("Contact: " + c);
			Node n = c.getNode();
			JLG.println("  Node: " + n);
			Map<Address, byte[]> localMap = dm.localMap(c);
			JLG.println("  Map: " + localMap);
		}
	}

	public synchronized void disconnectHard() throws Exception {
		super.disconnectHard();
		map.getLocalMap().clear();
	}

}