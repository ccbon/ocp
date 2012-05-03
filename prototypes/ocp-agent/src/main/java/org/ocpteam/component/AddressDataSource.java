package org.ocpteam.component;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.Serializable;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.ocpteam.entity.Address;
import org.ocpteam.entity.Contact;
import org.ocpteam.entity.EOMObject;
import org.ocpteam.entity.InputFlow;
import org.ocpteam.exception.NotAvailableContactException;
import org.ocpteam.interfaces.IAddressMap;
import org.ocpteam.interfaces.INodeMap;
import org.ocpteam.interfaces.IPersistentMap;
import org.ocpteam.misc.JLG;

public abstract class AddressDataSource extends DSPDataSource {

	protected INodeMap nodeMap;
	protected IAddressMap map;
	protected Map<Address, byte[]> localMap;
	public MessageDigest md;
	protected Random random;

	public AddressDataSource() throws Exception {
		super();
		addComponent(INodeMap.class, new RingNodeMap());
		addComponent(IAddressMap.class, new RingAddressMap());
		addComponent(MapModule.class);
		addComponent(RingMapModule.class);
		addComponent(MessageDigest.class);
		addComponent(Random.class);
		// addComponent(IPersistentMap.class, new PersistentFileMap());
	}

	@Override
	public void init() throws Exception {
		super.init();
		if (usesComponent(IPersistentMap.class)) {
			localMap = Collections
					.synchronizedMap(getComponent(IPersistentMap.class));
		} else {
			localMap = Collections
					.synchronizedMap(new HashMap<Address, byte[]>());
		}
		nodeMap = getComponent(INodeMap.class);
		map = getComponent(IAddressMap.class);
		map.setNodeMap(nodeMap);
		map.setLocalMap(localMap);
		md = getComponent(MessageDigest.class);
		random = getComponent(Random.class);
	}

	@Override
	protected void readNetworkConfig() throws Exception {
		super.readNetworkConfig();
		if (usesComponent(IPersistentMap.class)) {
			String dir = getProperty("localmap.dir", System.getenv("TEMP")
					+ "/" + getProtocolName());
			JLG.debug("dir=" + dir);
			getComponent(IPersistentMap.class).setRoot(dir);
		}
	}

	@Override
	protected void askForNode() throws Exception {
		super.askForNode();
		nodeMap.askForNode();
	}

	@Override
	protected void onNodeNiceDeparture() throws Exception {
		super.onNodeNiceDeparture();
		map.onNodeNiceDeparture();
	}

	@Override
	protected void onNodeArrival() throws Exception {
		super.onNodeArrival();
		map.onNodeArrival();
	}

	@Override
	public synchronized void disconnectHard() throws Exception {
		super.disconnectHard();
		localMap.clear();
	}

	public void networkPicture() throws Exception {
		map.networkPicture();
	}

	public Map<Address, byte[]> localMap(Contact c) throws Exception {
		if (c.isMyself()) {
			return map.getLocalMap();
		}
		Map<Address, byte[]> localmap = new HashMap<Address, byte[]>();
		MapModule m = getComponent(MapModule.class);
		InputFlow message = new InputFlow(m.getLocalMap());

		Socket socket = null;
		try {

			socket = contactMap.getTcpClient(c).borrowSocket(message);
			DataInputStream in = new DataInputStream(socket.getInputStream());
			while (true) {
				Serializable serializable = protocol.getStreamSerializer()
						.readObject(in);
				if (serializable instanceof EOMObject) {
					break;
				}
				Address key = (Address) serializable;
				byte[] value = (byte[]) protocol.getStreamSerializer()
						.readObject(in);

				localmap.put(key, value);
			}
			contactMap.getTcpClient(c).returnSocket(socket);
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

		return localmap;
	}

}
