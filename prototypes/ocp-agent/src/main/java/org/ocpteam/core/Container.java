package org.ocpteam.core;

public class Container<T extends IContainer> extends TopContainer implements
		IComponent {

	private IContainer parent;

	@Override
	public void setParent(IContainer parent) {
		this.parent = parent;
	}

	@Override
	public IContainer getParent() {
		return parent;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getRoot() {
		return (T) parent.getRoot();
	}

}
