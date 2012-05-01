package org.ocpteam.component;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.Serializable;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.ocpteam.entity.Address;
import org.ocpteam.entity.Contact;
import org.ocpteam.entity.EOMObject;
import org.ocpteam.entity.InputFlow;
import org.ocpteam.exception.NotAvailableContactException;
import org.ocpteam.interfaces.IAddressMap;
import org.ocpteam.interfaces.IMapDataModel;
import org.ocpteam.misc.JLG;

public class AddressMapDataModel extends DSContainer<DSPDataSource> implements
		IMapDataModel {

	IAddressMap getMap() {
		return ds().getComponent(IAddressMap.class);
	}
	
	public Address getAddress(String key) throws Exception {
		return new Address(ds().getComponent(MessageDigest.class).hash(key.getBytes()));
	}
	
	@Override
	public void set(String key, String value) throws Exception {
		Address address = getAddress(key);
		getMap().put(address, value.getBytes());
		setRootContent(key, address);
		JLG.debug("set finished.");
	}

	private void setRootContent(String key, Address address) throws Exception {
		HashMap<String, Address> directory = getRootContent();
		directory.put(key, address);
		getMap().put(getRootAddress(), JLG.serialize(directory));
	}

	private Address getRootAddress() throws Exception {
		return getAddress("qwerasdf!@#$1234");
	}

	@SuppressWarnings("unchecked")
	private HashMap<String, Address> getRootContent() throws Exception {
		byte[] root = getMap().get(getRootAddress());
		if (root == null) {
			return new HashMap<String, Address>();
		} else {
			return (HashMap<String, Address>) JLG.deserialize(root);
		}
	}

	@Override
	public String get(String key) throws Exception {
		HashMap<String, Address> directory = getRootContent();
		Address address = directory.get(key);
		if (address == null) {
			return null;
		}
		byte[] value = getMap().get(address);
		if (value != null) {
			return new String(value);
		} else {
			return null;
		}
	}

	@Override
	public void remove(String key) throws Exception {
		Address address = getAddress(key);
		getMap().remove(address);
		removeRootContent(key, address);

	}

	private void removeRootContent(String key, Address address)
			throws Exception {
		HashMap<String, Address> directory = getRootContent();
		directory.remove(key);
		getMap().put(getRootAddress(), JLG.serialize(directory));
	}

	@Override
	public Set<String> keySet() throws Exception {
		HashMap<String, Address> directory = getRootContent();
		return directory.keySet();
	}

	public Map<Address, byte[]> localMap(Contact c) throws Exception {
		if (c.isMyself()) {
			return getMap().getLocalMap();
		}
		Map<Address, byte[]> localmap = new HashMap<Address, byte[]>();
		MapModule m = ds().getComponent(MapModule.class);
		InputFlow message = new InputFlow(m.getLocalMap());

		Socket socket = null;
		try {

			socket = ds().contactMap.getTcpClient(c).borrowSocket(message);
			DataInputStream in = new DataInputStream(socket.getInputStream());
			while (true) {
				Serializable serializable = ds().protocol.getStreamSerializer()
						.readObject(in);
				if (serializable instanceof EOMObject) {
					break;
				}
				Address key = (Address) serializable;
				byte[] value = (byte[]) ds().protocol.getStreamSerializer()
						.readObject(in);

				localmap.put(key, value);
			}
			ds().contactMap.getTcpClient(c).returnSocket(socket);
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
