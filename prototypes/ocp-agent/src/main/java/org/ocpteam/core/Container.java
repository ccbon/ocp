package org.ocpteam.core;

import org.ocpteam.misc.JLG;

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
		JLG.debug("class=" + getClass());
		return parent.getRoot();
	}
	
}
