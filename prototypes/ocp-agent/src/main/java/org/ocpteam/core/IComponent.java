package org.ocpteam.core;

/**
 * A IComponent object is an object that has a parent. It has as well a root
 * which is the parent, or the parent of the parent...
 * 
 * 
 * 
 */
public interface IComponent {
	
	public void setParent(IContainer parent);

	public IContainer getParent();

	public IContainer getRoot();
}
