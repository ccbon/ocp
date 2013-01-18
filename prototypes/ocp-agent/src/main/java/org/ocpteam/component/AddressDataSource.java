package org.ocpteam.component;

import java.io.EOFException;
import java.io.Serializable;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import org.ocpteam.exception.NotAvailableContactException;
import org.ocpteam.interfaces.IAddressMap;
import org.ocpteam.interfaces.IDataStore;
import org.ocpteam.interfaces.INodeMap;
import org.ocpteam.interfaces.IPersistentMap;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.LOG;
import org.ocpteam.serializable.Address;
import org.ocpteam.serializable.Contact;
import org.ocpteam.serializable.EOMObject;
import org.ocpteam.serializable.InputFlow;
import org.ocpteam.ui.swt.DataSourceWindow;

public abstract class AddressDataSource extends DSPDataSource {

	protected INodeMap nodeMap;
	protected IAddressMap map;
	protected IDataStore localMap;
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
		addComponent(IDataStore.class, new DataStore());
	}

	@Override
	public void init() throws Exception {
		super.init();
		localMap = getComponent(IDataStore.class);
		nodeMap = getComponent(INodeMap.class);
		map = getComponent(IAddressMap.class);
		map.setNodeMap(nodeMap);
		map.setLocalMap(localMap);
		md = getComponent(MessageDigest.class);
		random = getComponent(Random.class);
	}

	@Override
	public void readConfig() throws Exception {
		super.readConfig();
		if (getComponent(IDataStore.class) instanceof IPersistentMap) {
			String dir = getProperty("uri", DataSourceWindow.GDSE_DIR
					+ "/datastore/" + getProtocolName() + "/" + getName());
			LOG.debug("dir=" + dir);
			String uri = getProperty("datastore.uri", dir);

			((IPersistentMap) getComponent(IDataStore.class)).setURI(uri);
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
		// localMap.clear();
	}

	public void networkPicture() throws Exception {
		map.networkPicture();
	}

	public IDataStore localMap(Contact c) throws Exception {
		if (c.isMyself()) {
			return map.getLocalMap();
		}
		IDataStore localmap = new DataStore();
		MapModule m = getComponent(MapModule.class);
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
				Address key = (Address) serializable;
				LOG.debug("address=" + key);
				byte[] value = (byte[]) protocol.getStreamSerializer()
						.readObject(socket);
				LOG.debug("sha1(value)=" + JLG.sha1(value));
				localmap.put(key, value);
			}
			contactMap.getTcpClient(c).returnSocket(socket);
			socket = null;
		} catch (SocketException e) {
		} catch (EOFException e) {
		} catch (SocketTimeoutException e) {
		} catch (NotAvailableContactException e) {
		} finally {
			if (socket != null) {
				LOG.debug("about to close the socket. For info socket buffer size:"
						+ socket.getReceiveBufferSize());
				socket.close();
				socket = null;
			}
		}

		return localmap;
	}

}
