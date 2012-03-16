package org.ocpteam.component;

import org.ocpteam.core.IComponent;
import org.ocpteam.core.IContainer;

public abstract class Agent implements IComponent {

	public DataSource ds;

	public abstract void connect() throws Exception;

	public abstract void disconnect() throws Exception;

	@Override
	public void setParent(IContainer parent) {
		ds = (DataSource) parent;
	}

}
