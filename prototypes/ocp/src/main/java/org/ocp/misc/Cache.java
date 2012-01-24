package org.ocp.misc;

import java.util.HashMap;
import java.util.Map;

public class Cache {

	Map<Object, Map<Object, Object>> cache;
	
	public Cache() {
		cache = new HashMap<Object, Map<Object, Object>>();
	}
	
	public void put(Object region, Object key, Object value) {
		Map<Object, Object> map = cache.get(region);
		if (map == null) {
			map = new HashMap<Object, Object>();
			cache.put(region, map);
		}
		map.put(key, value);
	}

	public Object get(Object region, Object key) {
		Map<Object, Object> map = cache.get(region);
		if (map == null) {
			map = new HashMap<Object, Object>();
			cache.put(region, map);
		}
		return map.get(key);
	}

}
