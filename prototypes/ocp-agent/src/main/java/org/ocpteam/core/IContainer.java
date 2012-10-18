package org.ocpteam.core;

import java.util.Collection;
import java.util.Iterator;



public interface IContainer extends IConfig {
	public Designer getDesigner();

	public IContainer getRoot();
	
	public <T> boolean usesComponent(Class<T> c);
	
	public <T> boolean usesComponent(String name, Class<T> c);

	public <T> T getComponent(Class<T> c);
	
	public <T> T getComponent(String name, Class<T> c);

	public <T> T addComponent(Class<T> c) throws Exception;
	
	public <T> T addComponent(String name, Class<T> c) throws Exception;

	public <T> T addComponent(Class<T> c, T instance)
			throws Exception;
	
	public <T> T addComponent(String name, Class<T> c, T instance)
			throws Exception;

	public <T> void replaceComponent(Class<T> c, T instance)
			throws Exception;
	
	public <T> void replaceComponent(String name, Class<T> c, T instance)
			throws Exception;

	public <T> T removeComponent(Class<T> c);
	
	public <T> T removeComponent(String name, Class<T> c);
	
	public Iterator<Object> iteratorComponent();
	
	public Collection<Object> components();
	
	public void init() throws Exception;

}
