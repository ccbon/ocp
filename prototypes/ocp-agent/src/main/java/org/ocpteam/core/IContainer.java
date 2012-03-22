package org.ocpteam.core;

import java.util.Iterator;

import org.ocpteam.interfaces.IConfig;


public interface IContainer extends IConfig {
	public Designer getDesigner();

	public IContainer getRoot();
	
	public <T extends IComponent> boolean usesComponent(Class<T> c);

	public <T extends IComponent> T getComponent(Class<T> c);

	public <T extends IComponent> T addComponent(Class<T> c) throws Exception;

	public <T extends IComponent> T addComponent(Class<T> c, T instance)
			throws Exception;

	public <T extends IComponent> void replaceComponent(Class<T> c, T instance)
			throws Exception;

	public <T extends IComponent> T removeComponent(Class<T> c);
	
	public Iterator<IComponent> iteratorComponent();

}
