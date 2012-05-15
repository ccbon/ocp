package org.ocpteam.component;

import java.util.HashMap;
import java.util.Set;

import org.ocpteam.interfaces.IAddressMap;
import org.ocpteam.interfaces.IMapDataModel;
import org.ocpteam.misc.JLG;
import org.ocpteam.serializable.Address;

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
		getMap().put(getRootAddress(), ds().serializer.serialize(directory));
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
			return (HashMap<String, Address>) ds().serializer.deserialize(root);
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
		getMap().put(getRootAddress(), ds().serializer.serialize(directory));
	}

	@Override
	public Set<String> keySet() throws Exception {
		HashMap<String, Address> directory = getRootContent();
		return directory.keySet();
	}

}
