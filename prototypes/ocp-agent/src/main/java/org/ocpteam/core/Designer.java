package org.ocpteam.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class Designer<P extends IContainer> {

	private Map<Class<IComponent>, IComponent> map;

	private P parent;
	
	public Designer(P parent) {
		map = new HashMap<Class<IComponent>, IComponent>();
		this.parent = parent;
	}
	
	public P getParent() {
		return parent;
	}

	public boolean uses(Object functionality) {
		return map.containsKey(functionality);
	}

	@SuppressWarnings("unchecked")
	public <T extends IComponent> T get(Class<T> functionality) {
		return (T) map.get(functionality);
	}

	@SuppressWarnings("unchecked")
	public <T extends IComponent> void add(Class<T> functionality) throws Exception {
		if (!map.containsKey(functionality)) {
			T instance = functionality.newInstance();
			instance.setParent(parent);
			map.put((Class<IComponent>) functionality, instance);
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends IComponent> void add(Class<T> functionality, T instance) throws Exception {
		if (!map.containsKey(functionality)) {
			instance.setParent(parent);
			map.put((Class<IComponent>) functionality, instance);
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T extends IComponent> void replace(Class<T> functionality, T instance) throws Exception {
		if (map.containsKey(functionality)) {
			instance.setParent(parent);
			map.put((Class<IComponent>) functionality, instance);
		}  else {
			throw new Exception("functionality not existing");
		}
	}

	public Iterator<IComponent> iterator() {
		return map.values().iterator();
	}

	@SuppressWarnings("unchecked")
	public <T extends IComponent> T remove(Class<T> functionality) {
		return (T) map.remove(functionality);
	}


}
