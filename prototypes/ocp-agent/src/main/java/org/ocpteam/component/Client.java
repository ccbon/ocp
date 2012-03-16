package org.ocpteam.component;

import org.ocpteam.core.IComponent;
import org.ocpteam.core.IContainer;

public class Client implements IComponent, IClient {

	protected DataSource ds;

	@Override
	public void setParent(IContainer parent) {
		this.ds = (DataSource) parent;	
	}

}
