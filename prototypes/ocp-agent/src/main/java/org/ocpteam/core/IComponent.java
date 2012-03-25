package org.ocpteam.core;


public interface IComponent {
	public void setParent(IContainer parent);
	public void init() throws Exception;
	public IContainer getParent();
	public IContainer getRoot();
}
