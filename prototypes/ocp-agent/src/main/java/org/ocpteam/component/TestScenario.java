package org.ocpteam.component;

import org.ocpteam.core.IContainer;
import org.ocpteam.core.IComponent;

public abstract class TestScenario implements IComponent {

	protected IContainer parent;

	@Override
	public void setParent(IContainer parent) {
		this.parent = parent;
	}
	
	public abstract boolean test();

}
