package org.ocpteam.core;

import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

import org.ocpteam.misc.JLG;

public class TopContainer implements IContainer {

	protected Properties p = new Properties();

	private Designer designer;

	@Override
	public <T> boolean usesComponent(Class<T> c) {
		return designer.uses(c);
	}

	@Override
	public <T> T getComponent(Class<T> c) {
		return designer.get(c);
	}

	@Override
	public <T> T addComponent(Class<T> c) throws Exception {
		return designer.add(c);
	}

	@Override
	public <T> T addComponent(Class<T> c, T instance)
			throws Exception {
		return designer.add(c, instance);
	}

	@Override
	public <T> void replaceComponent(Class<T> c, T instance)
			throws Exception {
		designer.replace(c, instance);
	}

	@Override
	public <T> T removeComponent(Class<T> c) {
		return designer.remove(c);
	}

	@Override
	public Iterator<Object> iteratorComponent() {
		return designer.iterator();
	}

	public TopContainer() {
		designer = new Designer(this);
	}

	@Override
	public Designer getDesigner() {
		return designer;
	}

	@Override
	public void setConfig(Properties p) {
		this.p = p;
	}

	@Override
	public Properties getConfig() {
		return p;
	}

	@Override
	public String getProperty(String key) {
		return p.getProperty(key);
	}

	@Override
	public String getProperty(String key, String defaultValue) {
		return p.getProperty(key, defaultValue);
	}

	@Override
	public void setProperty(String key, String value) {
		p.setProperty(key, value);
	}

	@Override
	public Iterator<String> iterator() {
		return p.stringPropertyNames().iterator();
	}

	@Override
	public IContainer getRoot() {
		return this;
	}

	@Override
	public void init() throws Exception {
		JLG.debug("init class " + getClass());
		Iterator<Object> it = designer.iterator();
		while (it.hasNext()) {
			Object o = it.next();
			if (o instanceof IContainer) {
				((IContainer) o).init();
			}
		}

	}

	@Override
	public Collection<Object> components() {
		return designer.getMap().values();
	}

}
