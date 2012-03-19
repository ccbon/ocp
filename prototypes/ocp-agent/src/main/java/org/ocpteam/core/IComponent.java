package org.ocpteam.core;


public interface IComponent {
	public void setParent(IContainer parent);
	public IContainer getParent();
}
