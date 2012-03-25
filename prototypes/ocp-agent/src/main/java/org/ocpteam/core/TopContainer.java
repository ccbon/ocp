package org.ocpteam.core;

import java.util.Iterator;
import java.util.Properties;

import org.ocpteam.misc.JLG;


public class TopContainer implements IContainer {
	
	protected Properties p = new Properties();
	
	private Designer designer;
	
	public <T extends IComponent> boolean usesComponent(Class<T> c) {
		return designer.uses(c);
	}

	public <T extends IComponent> T getComponent(Class<T> c) {
		return designer.get(c);
	}

	public <T extends IComponent> T addComponent(Class<T> c) throws Exception {
		return designer.add(c);
	}

	public <T extends IComponent> T addComponent(Class<T> c, T instance)
			throws Exception {
		return designer.add(c, instance);
	}

	public <T extends IComponent> void replaceComponent(Class<T> c, T instance)
			throws Exception {
		designer.replace(c, instance);
	}

	public <T extends IComponent> T removeComponent(Class<T> c) {
		return designer.remove(c);
	}
	
	public Iterator<IComponent> iteratorComponent() {
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
	public String get(String key) {
		return p.getProperty(key);
	}

	@Override
	public String get(String key, String defaultValue) {
		return p.getProperty(key, defaultValue);
	}

	@Override
	public void set(String key, String value) {
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
		Iterator<IComponent> it = designer.iterator();
		while (it.hasNext()) {
			it.next().init();
		}
		
	}


}
