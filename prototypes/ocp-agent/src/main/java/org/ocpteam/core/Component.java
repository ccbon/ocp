package org.ocpteam.core;

public class Component implements IComponent {

	protected IContainer parent;

	@Override
	public void setParent(IContainer parent) {
		this.parent = parent;
	}

}