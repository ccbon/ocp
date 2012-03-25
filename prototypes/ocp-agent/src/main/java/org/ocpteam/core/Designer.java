package org.ocpteam.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class Designer {

	private Map<Class<IComponent>, IComponent> map;

	private IContainer parent;
	
	public Designer(IContainer parent) {
		map = new HashMap<Class<IComponent>, IComponent>();
		this.parent = parent;
	}
	
	public IContainer getParent() {
		return parent;
	}

	public <T extends IComponent> boolean uses(Class<T> c) {
		return map.containsKey(c);
	}

	@SuppressWarnings("unchecked")
	public <T extends IComponent> T get(Class<T> c) {
		return (T) map.get(c);
	}

	@SuppressWarnings("unchecked")
	public <T extends IComponent> T add(Class<T> c) throws Exception {
		if (!map.containsKey(c)) {
			T instance = c.newInstance();
			instance.setParent(parent);
			map.put((Class<IComponent>) c, instance);
		}
		return (T) map.get(c);
	}

	@SuppressWarnings("unchecked")
	public <T extends IComponent> T add(Class<T> c, T instance) throws Exception {
		if (!map.containsKey(c)) {
			instance.setParent(parent);
			map.put((Class<IComponent>) c, instance);
		}
		return (T) map.get(c);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends IComponent> void replace(Class<T> c, T instance) throws Exception {
		if (map.containsKey(c)) {
			instance.setParent(parent);
			map.put((Class<IComponent>) c, instance);
		}  else {
			throw new Exception("functionality not existing");
		}
	}

	public Iterator<IComponent> iterator() {
		return map.values().iterator();
	}

	@SuppressWarnings("unchecked")
	public <T extends IComponent> T remove(Class<T> c) {
		return (T) map.remove(c);
	}
	
	public Map<Class<IComponent>, IComponent> getMap() {
		return map;
	}


}
