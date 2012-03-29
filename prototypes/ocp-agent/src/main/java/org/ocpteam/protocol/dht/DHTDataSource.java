package org.ocpteam.protocol.dht;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.ocpteam.component.DSPDataSource;
import org.ocpteam.entity.Context;
import org.ocpteam.interfaces.IDataModel;

public class DHTDataSource extends DSPDataSource {

	private Map<String, String> map;

	public DHTDataSource() throws Exception {
		super();
		addComponent(IDataModel.class, new DHTDataModel());
		addComponent(DHTModule.class);
	}
	
	@Override
	public void init() throws Exception {
		super.init();
		map = Collections.synchronizedMap(new HashMap<String, String>());
	}
	
	@Override
	public String getProtocol() {
		return "DHT";
	}
	
	@Override
	public void connect() throws Exception {
		super.connect();
		Context c = new Context(getComponent(IDataModel.class));
		setContext(c);
	}

	public void store(String key, String value) {
		map.put(key, value);
	}

	public String retrieve(String key) {
		return map.get(key);
	}

	public void remove(String key) {
		map.remove(key);
	}

	public Set<String> keySet() {
		return map.keySet();
	}


	

}
