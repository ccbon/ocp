package org.ocpteam.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public class Designer {

	private Map<Class<Object>, Map<String, Object>> map;

	private IContainer parent;

	public Designer(IContainer parent) {
		map = new HashMap<Class<Object>, Map<String, Object>>();
		this.parent = parent;
	}

	public IContainer getParent() {
		return parent;
	}

	public <T> boolean uses(String name, Class<T> c) {
		if (!map.containsKey(c)) {
			return false;
		}
		Map<String, Object> m = map.get(c);
		return m.containsKey(name);
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String name, Class<T> c) {
		if (!map.containsKey(c)) {
			return null;
		}
		Map<String, Object> m = map.get(c);
		return (T) m.get(name);
	}

	@SuppressWarnings("unchecked")
	public <T> T add(String name, Class<T> c) throws Exception {
		if (!map.containsKey(c)) {
			map.put((Class<Object>) c, new HashMap<String, Object>());
		}
		Map<String, Object> m = map.get(c);
		T instance = c.newInstance();
		if (instance instanceof IComponent) {
			((IComponent) instance).setParent(parent);
		}
		m.put(name, instance);
		return (T) map.get(c).get(name);
	}

	@SuppressWarnings("unchecked")
	public <T> T add(String name, Class<T> c, T instance) throws Exception {
		if (!map.containsKey(c)) {
			map.put((Class<Object>) c, new HashMap<String, Object>());
		}
		Map<String, Object> m = map.get(c);
		if (instance instanceof IComponent) {
			((IComponent) instance).setParent(parent);
		}
		m.put(name, instance);
		return (T) map.get(c).get(name);
	}

	public <T> void replace(String name, Class<T> c, T instance)
			throws Exception {
		if (map.containsKey(c) && map.get(c).containsKey(name)) {
			if (instance instanceof IComponent) {
				((IComponent) instance).setParent(parent);
			}
			map.get(c).put(name, instance);
		} else {
			throw new Exception("functionality not existing");
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T remove(String name, Class<T> c) {
		if (!map.containsKey(c)) {
			return null;
		}
		Map<String, Object> m = map.get(c);
		if (!m.containsKey(name)) {
			return null;
		}
		return (T) m.remove(name);
	}

	public Iterator<Object> iterator() {
		return components().iterator();
	}

	public Map<Class<Object>, Map<String, Object>> getMap() {
		return map;
	}

	public Collection<Object> components() {
		Collection<Object> result = new HashSet<Object>();
		for (Map<String, Object> m : map.values()) {
			for (Object o : m.values()) {
				result.add(o);
			}
		}
		return result;
	}

}
