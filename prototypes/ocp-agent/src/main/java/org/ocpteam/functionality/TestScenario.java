package org.ocpteam.functionality;

import org.ocpteam.core.Container;
import org.ocpteam.core.Functionality;

public abstract class TestScenario implements Functionality {

	protected Container parent;

	@Override
	public void setParent(Container parent) {
		this.parent = parent;
	}
	
	public abstract boolean test();

}
