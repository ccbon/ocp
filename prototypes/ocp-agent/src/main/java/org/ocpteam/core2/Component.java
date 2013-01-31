package org.ocpteam.core2;

import java.util.Iterator;
import java.util.Map;

import org.ocpteam.misc.LOG;

public class Component implements IComponent {

	private Designer designer;
	private IComponent parent;

	public Component() {
		designer = new Designer(this);
	}

	@Override
	public <T> boolean usesComponent(Class<T> c) {
		return designer.uses("", c);
	}

	@Override
	public <T> boolean usesComponent(String name, Class<T> c) {
		return designer.uses(name, c);
	}

	@Override
	public <T> T getComponent(Class<T> c) {
		return designer.get("", c);
	}

	@Override
	public <T> T getComponent(String name, Class<T> c) {
		return designer.get(name, c);
	}

	@Override
	public <T> T addComponent(Class<T> c) throws Exception {
		return designer.add("", c);
	}

	@Override
	public <T> T addComponent(String name, Class<T> c) throws Exception {
		return designer.add(name, c);
	}

	@Override
	public <T> T addComponent(Class<T> c, T instance) throws Exception {
		return designer.add("", c, instance);
	}

	@Override
	public <T> T addComponent(String name, Class<T> c, T instance)
			throws Exception {
		return designer.add(name, c, instance);
	}

	@Override
	public <T> void replaceComponent(Class<T> c, T instance) throws Exception {
		designer.replace("", c, instance);
	}

	@Override
	public <T> void replaceComponent(String name, Class<T> c, T instance)
			throws Exception {
		designer.replace(name, c, instance);

	}

	@Override
	public <T> T removeComponent(Class<T> c) {
		return designer.remove("", c);
	}

	@Override
	public <T> T removeComponent(String name, Class<T> c) {
		return designer.remove(name, c);
	}

	@Override
	public void init() throws Exception {
		LOG.info("init class " + getClass());
		Iterator<Object> it = designer.components().iterator();
		while (it.hasNext()) {
			Object o = it.next();
			if (o instanceof IComponent) {
				((IComponent) o).init();
			}
		}
	}

	@Override
	public IComponent parent() {
		return parent;
	}

	@Override
	public void setParent(IComponent parent) throws Exception {
		this.parent = parent;
	}

	@Override
	public IComponent master() throws Exception {
		IComponent result = this;
		while (!result.isMaster()) {
			result = result.parent();
			if (result == null) {
				throw new Exception("Cannot find the master.");
			}
		}
		return result;
	}

	@Override
	public IComponent top() throws Exception {
		IComponent result = this;
		while (result.parent() != null) {
			result = result.parent();
		}
		return result;
	}

	@Override
	public Map<Class<Object>, Map<String, Object>> componentMap() {
		return designer.componentMap();
	}

	@Override
	public boolean isMaster() {
		return false;
	}
}
