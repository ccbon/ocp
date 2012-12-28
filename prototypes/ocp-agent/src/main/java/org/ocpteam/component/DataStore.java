package org.ocpteam.component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.ocpteam.interfaces.IDataStore;
import org.ocpteam.serializable.Address;

public class DataStore implements IDataStore {

	Map<Address, byte[]> map = new HashMap<Address, byte[]>();

	@Override
	public void put(Address a, byte[] b) {
		map.put(a, b);
	}

	@Override
	public byte[] get(Address a) {
		return map.get(a);
	}

	@Override
	public void remove(Address address) {
		map.remove(address);
	}

	@Override
	public boolean containsKey(Address address) {
		return map.containsKey(address);
	}

	@Override
	public Set<Address> keySet() {
		return map.keySet();
	}

	@Override
	public void putAll(IDataStore datastore) throws Exception {
		for (Address address : datastore.keySet()) {
			map.put(address, datastore.get(address));
		}
	}

	@Override
	public void clear() {
		map.clear();
	}

}
