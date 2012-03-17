package org.ocpteam.component;

import java.util.Properties;

import org.ocpteam.core.IComponent;
import org.ocpteam.core.IContainer;

public class Server implements IComponent {

	private PropertiesDataSource parent;

	@Override
	public void setParent(IContainer parent) {
		this.parent = (PropertiesDataSource) parent;
	}

	public boolean isStarted() {
		Properties cfg = parent.getProperties();
		return cfg.getProperty("server", "yes").equals("yes");
	}

}
