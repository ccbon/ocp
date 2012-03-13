package org.ocpteam.functionality;

import java.util.Properties;

import org.ocpteam.core.Container;
import org.ocpteam.core.Functionality;
import org.ocpteam.layer.rsp.PropertiesDataSource;

public class Server implements Functionality {

	private PropertiesDataSource parent;

	@Override
	public void setParent(Container parent) {
		this.parent = (PropertiesDataSource) parent;
	}

	public boolean isStarted() {
		Properties cfg = parent.getProperties();
		return cfg.getProperty("server", "yes").equals("yes");
	}

}
