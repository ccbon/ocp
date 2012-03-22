package org.ocpteam.core;


public interface IComponent {
	public void setParent(IContainer parent);
	public void init();
	public IContainer getParent();
	public IContainer getRoot();
}
