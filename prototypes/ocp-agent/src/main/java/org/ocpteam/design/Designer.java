package org.ocpteam.design;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Designer<P> {

	private Map<Class<Functionality<P>>, Functionality<P>> map;

	private P parent;
	
	public Designer(P parent) {
		map = new HashMap<Class<Functionality<P>>, Functionality<P>>();
		this.parent = parent;
	}
	
	public P getParent() {
		return parent;
	}

	public boolean uses(Object functionality) {
		return map.containsKey(functionality);
	}

	@SuppressWarnings("unchecked")
	public <T extends Functionality<P>> T get(Class<T> functionality) {
		return (T) map.get(functionality);
	}

	@SuppressWarnings("unchecked")
	public <T extends Functionality<P>> void add(Class<T> functionality) throws Exception {
		if (!map.containsKey(functionality)) {
			T instance = functionality.newInstance();
			instance.setParent(parent);
			map.put((Class<Functionality<P>>) functionality, instance);
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends Functionality<P>> void add(Class<T> functionality, T instance) throws Exception {
		if (!map.containsKey(functionality)) {
			instance.setParent(parent);
			map.put((Class<Functionality<P>>) functionality, instance);
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Functionality<P>> void replace(Class<T> functionality, T instance) throws Exception {
		if (map.containsKey(functionality)) {
			instance.setParent(parent);
			map.put((Class<Functionality<P>>) functionality, instance);
		}  else {
			throw new Exception("functionality not existing");
		}
	}

	public Iterator<Functionality<P>> iterator() {
		return map.values().iterator();
	}

	@SuppressWarnings("unchecked")
	public <T extends Functionality<P>> T remove(Class<T> functionality) {
		return (T) map.remove(functionality);
	}


}
