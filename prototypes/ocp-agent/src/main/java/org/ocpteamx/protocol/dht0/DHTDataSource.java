package org.ocpteamx.protocol.dht0;

import java.util.HashSet;
import java.util.Set;

import org.ocpteam.component.DSPDataSource;
import org.ocpteam.component.DataStore;
import org.ocpteam.interfaces.IDataModel;
import org.ocpteam.interfaces.IDataStore;
import org.ocpteam.misc.LOG;
import org.ocpteam.serializable.Address;

/**
 * DHT is a distributed hashtable, very minimalist and naive. Strategies: - Each
 * agent store all hashtable data (replication everywhere) - DataModel is Map
 * model (key->value set) - synchronization when connecting
 * 
 * Potentials issues: - if 2 agents tries to set a same key with different value
 * at same time, result is unpredictable.
 * 
 */
public class DHTDataSource extends DSPDataSource {

	private DHTDataModel dm;
	private IDataStore map;

	public DHTDataSource() throws Exception {
		super();
		addComponent(IDataModel.class, new DHTDataModel());
		addComponent(DHTModule.class);
		addComponent(IDataStore.class, new DataStore());
	}

	@Override
	public void init() throws Exception {
		super.init();
		map = getComponent(IDataStore.class);
		dm = (DHTDataModel) getComponent(IDataModel.class);
	}

	@Override
	public String getProtocolName() {
		return "DHT";
	}

	@Override
	public synchronized void connect() throws Exception {
		super.connect();
		// this will reload the internal map.
		Set<String> set = dm.keySet();
		for (String s : set) {
			Address a = new Address(s.getBytes());
			if (!map.containsKey(a)) {
				LOG.debug("synchronize " + s);
				map.put(a, dm.get(s).getBytes());
			}
		}
	}

	public void store(String key, String value) throws Exception {
		LOG.debug("local store: " + key + "->" + value);
		Address a = new Address(key.getBytes());
		map.put(a, value.getBytes());
	}

	public String retrieve(String key) throws Exception {
		LOG.debug("local retrieve: " + key);
		Address a = new Address(key.getBytes());
		byte[] b = map.get(a);
		if (b == null) {
			return null;
		}
		return new String(b);
	}

	public void remove(String key) throws Exception {
		Address a = new Address(key.getBytes());
		map.remove(a);
	}

	public Set<String> keySet() throws Exception {
		Set<Address> set = map.keySet();
		Set<String> result = new HashSet<String>();
		for (Address a : set) {
			result.add(new String(a.getBytes()));
		}
		return result;
	}

}
