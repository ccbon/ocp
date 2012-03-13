package org.ocpteam.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class Designer {

	private Map<Class<Functionality>, Functionality> map;

	private Container parent;
	
	public Designer(Container parent) {
		map = new HashMap<Class<Functionality>, Functionality>();
		this.parent = parent;
	}
	
	public Container getParent() {
		return parent;
	}

	public boolean uses(Object functionality) {
		return map.containsKey(functionality);
	}

	@SuppressWarnings("unchecked")
	public <T extends Functionality> T get(Class<T> functionality) {
		return (T) map.get(functionality);
	}

	@SuppressWarnings("unchecked")
	public <T extends Functionality> void add(Class<T> functionality) throws Exception {
		if (!map.containsKey(functionality)) {
			T instance = functionality.newInstance();
			instance.setParent(parent);
			map.put((Class<Functionality>) functionality, instance);
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends Functionality> void add(Class<T> functionality, T instance) throws Exception {
		if (!map.containsKey(functionality)) {
			instance.setParent(parent);
			map.put((Class<Functionality>) functionality, instance);
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Functionality> void replace(Class<T> functionality, T instance) throws Exception {
		if (map.containsKey(functionality)) {
			instance.setParent(parent);
			map.put((Class<Functionality>) functionality, instance);
		}  else {
			throw new Exception("functionality not existing");
		}
	}

	public Iterator<Functionality> iterator() {
		return map.values().iterator();
	}

	@SuppressWarnings("unchecked")
	public <T extends Functionality> T remove(Class<T> functionality) {
		return (T) map.remove(functionality);
	}


}
