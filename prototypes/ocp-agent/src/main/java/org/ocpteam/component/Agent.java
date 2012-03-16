package org.ocpteam.component;

import org.ocpteam.core.IComponent;
import org.ocpteam.core.IContainer;

public abstract class Agent implements IComponent, IAgent {

	public DataSource ds;

	@Override
	public void setParent(IContainer parent) {
		ds = (DataSource) parent;
	}

}
