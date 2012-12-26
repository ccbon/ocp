package org.ocpteam.core2;

import java.util.Map;

public interface IComponent {

	public IComponent parent();

	public void setParent(IComponent parent) throws Exception;

	public IComponent master() throws Exception;

	public boolean isMaster();

	public IComponent top() throws Exception;

	public <T> boolean usesComponent(Class<T> c);

	public <T> boolean usesComponent(String name, Class<T> c);

	public <T> T getComponent(Class<T> c);

	public <T> T getComponent(String name, Class<T> c);

	public <T> T addComponent(Class<T> c) throws Exception;

	public <T> T addComponent(String name, Class<T> c) throws Exception;

	public <T> T addComponent(Class<T> c, T instance) throws Exception;

	public <T> T addComponent(String name, Class<T> c, T instance)
			throws Exception;

	public <T> void replaceComponent(Class<T> c, T instance) throws Exception;

	public <T> void replaceComponent(String name, Class<T> c, T instance)
			throws Exception;

	public <T> T removeComponent(Class<T> c);

	public <T> T removeComponent(String name, Class<T> c);

	public Map<Class<Object>, Map<String, Object>> componentMap();

	public void init() throws Exception;
}
