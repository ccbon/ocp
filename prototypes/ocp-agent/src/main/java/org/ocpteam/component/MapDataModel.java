package org.ocpteam.component;

import java.util.Map;
import java.util.Set;

import org.ocpteam.core.Container;
import org.ocpteam.interfaces.IMapDataModel;

public class MapDataModel extends Container<DataSource> implements IMapDataModel {

	protected Map<String, String> map;
	
	public void setMap(Map<String, String> map) {
		this.map = map;
	}

	public Map<String, String> getMap() {
		return this.map;
	}

	@Override
	public void set(String key, String value) throws Exception {
		map.put(key, value);
	}

	@Override
	public String get(String key) throws Exception {
		return map.get(key);
	}

	@Override
	public void remove(String key) throws Exception {
		map.remove(key);
	}

	@Override
	public Set<String> keySet() throws Exception {
		return map.keySet();
	}


}
