package org.ocpteam.functionality;

import org.ocpteam.core.Application;
import org.ocpteam.design.Functionality;

public abstract class TestScenario implements Functionality<Application> {

	protected Application app;

	@Override
	public void setParent(Application parent) {
		this.app = parent;
	}
	
	public abstract boolean test();

}
