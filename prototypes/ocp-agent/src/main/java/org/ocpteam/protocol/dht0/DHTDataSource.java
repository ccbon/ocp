package org.ocpteam.protocol.dht0;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.ocpteam.component.DSPDataSource;
import org.ocpteam.entity.Context;
import org.ocpteam.interfaces.IDataModel;
import org.ocpteam.misc.JLG;

/**
 * DHT is a distributed hashtable, very minimalist and naive.
 * Strategies:
 * - Each agent store all hashtable data (replication everywhere)
 * - DataModel is Map model (key->value set)
 *
 */
public class DHTDataSource extends DSPDataSource {

	private Map<String, String> map;
	private DHTDataModel dm;

	public DHTDataSource() throws Exception {
		super();
		addComponent(IDataModel.class, new DHTDataModel());
		addComponent(DHTModule.class);
	}
	
	@Override
	public void init() throws Exception {
		super.init();
		map = Collections.synchronizedMap(new HashMap<String, String>());
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
			if (!map.containsKey(s)) {
				JLG.debug("synchronize " + s);
				map.put(s, dm.get(s));
			}
		}
		Context c = new Context(getComponent(IDataModel.class));
		setContext(c);
	}

	public void store(String key, String value) {
		JLG.debug("local store: " + key + "->" + value);
		map.put(key, value);
	}

	public String retrieve(String key) {
		JLG.debug("local retrieve: " + key);
		return map.get(key);
	}

	public void remove(String key) {
		map.remove(key);
	}

	public Set<String> keySet() {
		return map.keySet();
	}


	

}
