package org.ocpteam.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Designer {

	private Map<Class<Object>, Object> map;

	private IContainer parent;

	public Designer(IContainer parent) {
		map = new HashMap<Class<Object>, Object>();
		this.parent = parent;
	}

	public IContainer getParent() {
		return parent;
	}

	public <T> boolean uses(Class<T> c) {
		return map.containsKey(c);
	}

	@SuppressWarnings("unchecked")
	public <T> T get(Class<T> c) {
		return (T) map.get(c);
	}

	@SuppressWarnings("unchecked")
	public <T> T add(Class<T> c) throws Exception {
		if (!map.containsKey(c)) {
			T instance = c.newInstance();
			if (instance instanceof IComponent) {
				((IComponent) instance).setParent(parent);
			}
			map.put((Class<Object>) c, instance);
		}
		return (T) map.get(c);
	}

	@SuppressWarnings("unchecked")
	public <T> T add(Class<T> c, T instance) throws Exception {
		if (!map.containsKey(c)) {
			if (instance instanceof IComponent) {
				((IComponent) instance).setParent(parent);
			}
			map.put((Class<Object>) c, instance);
		}
		return (T) map.get(c);
	}

	@SuppressWarnings("unchecked")
	public <T> void replace(Class<T> c, T instance)
			throws Exception {
		if (map.containsKey(c)) {
			if (instance instanceof IComponent) {
				((IComponent) instance).setParent(parent);
			}
			map.put((Class<Object>) c, instance);
		} else {
			throw new Exception("functionality not existing");
		}
	}

	public Iterator<Object> iterator() {
		return map.values().iterator();
	}

	@SuppressWarnings("unchecked")
	public <T> T remove(Class<T> c) {
		return (T) map.remove(c);
	}

	public Map<Class<Object>, Object> getMap() {
		return map;
	}

}
