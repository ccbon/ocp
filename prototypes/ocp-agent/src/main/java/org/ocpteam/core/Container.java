package org.ocpteam.core;


public class Container extends TopContainer implements IComponent {

	private IContainer parent;

	@Override
	public void setParent(IContainer parent) {
		this.parent = parent;
	}

	@Override
	public IContainer getParent() {
		return parent;
	}
	
	@Override
	public IContainer getRoot() {
		return parent.getRoot();
	}

	@Override
	public void init() throws Exception {
	}
	
}
