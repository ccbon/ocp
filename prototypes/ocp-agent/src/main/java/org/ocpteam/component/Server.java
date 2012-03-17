package org.ocpteam.component;

import java.util.Properties;

import org.ocpteam.core.IComponent;
import org.ocpteam.core.IContainer;

public class Server implements IComponent, IServer {

	private DataSource parent;

	@Override
	public void setParent(IContainer parent) {
		this.parent = (DataSource) parent;
	}

	public boolean isStarted() {
		Properties cfg = parent.getConfig();
		return cfg.getProperty("server", "yes").equals("yes");
	}

}
