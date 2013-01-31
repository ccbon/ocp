package org.ocpteam.core;

import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

import org.ocpteam.misc.LOG;

public class TopContainer implements IContainer {

	private Properties p = new Properties();

	private Designer designer;

	public TopContainer() {
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
	public <T> T addComponent(Class<T> c, T instance)
			throws Exception {
		return designer.add("", c, instance);
	}

	@Override
	public <T> T addComponent(String name, Class<T> c, T instance)
			throws Exception {
		return designer.add(name, c, instance);
	}

	@Override
	public <T> void replaceComponent(Class<T> c, T instance)
			throws Exception {
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
	public Iterator<Object> iteratorComponent() {
		return designer.iterator();
	}

	@Override
	public Designer getDesigner() {
		return designer;
	}

	@Override
	public void setConfig(Properties p) throws Exception {
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
		LOG.info("init class " + getClass());
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
		return designer.components();
	}
}
