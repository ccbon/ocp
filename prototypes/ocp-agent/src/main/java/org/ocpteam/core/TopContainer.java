package org.ocpteam.core;

import java.util.Iterator;
import java.util.Properties;


public class TopContainer implements IContainer {
	
	protected Properties p = new Properties();
	
	private Designer designer;
	
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


}
