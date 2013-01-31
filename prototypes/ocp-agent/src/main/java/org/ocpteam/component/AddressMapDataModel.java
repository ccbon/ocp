package org.ocpteam.component;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import org.ocpteam.interfaces.IAddressMap;
import org.ocpteam.interfaces.IMapDataModel;
import org.ocpteam.misc.LOG;
import org.ocpteam.serializable.Address;
import org.ocpteam.serializable.RootContent;

public class AddressMapDataModel extends DSContainer<DSPDataSource> implements
		IMapDataModel {

	IAddressMap getMap() {
		return ds().getComponent(IAddressMap.class);
	}

	public Address getAddress(String key) throws Exception {
		return new Address(ds().getComponent(MessageDigest.class).hash(
				key.getBytes()));
	}

	@Override
	public void set(String key, String value) throws Exception {
		Address address = getAddress(key);
		getMap().put(address, value.getBytes());
		setRootContent(key, address);
		LOG.info("set finished.");
	}

	private void setRootContent(String key, Address address) throws Exception {
		RootContent rootContent = getRootContent();
		Map<String, Address> directory = rootContent.getMap();
		directory.put(key, address);
		getMap().put(getRootAddress(),
				ds().serializer.serialize((Serializable) rootContent));
	}

	private Address getRootAddress() throws Exception {
		return getAddress("qwerasdf!@#$1234");
	}

	private RootContent getRootContent() throws Exception {
		byte[] root = getMap().get(getRootAddress());
		if (root == null) {
			return new RootContent();
		} else {
			return (RootContent) ds().serializer.deserialize(root);
		}
	}

	@Override
	public String get(String key) throws Exception {
		Map<String, Address> directory = getRootContent().getMap();
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
		RootContent rootContent = getRootContent();
		Map<String, Address> directory = rootContent.getMap();
		directory.remove(key);
		getMap().put(getRootAddress(),
				ds().serializer.serialize(rootContent));
	}

	@Override
	public Set<String> keySet() throws Exception {
		Map<String, Address> directory = getRootContent().getMap();
		return directory.keySet();
	}

}
