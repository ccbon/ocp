package org.ocpteam.layer.rsp;

import java.util.Properties;

import org.ocpteam.design.Functionality;

public class Server implements Functionality<DataSource> {

	private PropertiesDataSource parent;

	@Override
	public void setParent(DataSource parent) {
		this.parent = (PropertiesDataSource) parent;
	}

	public boolean isStarted() {
		Properties cfg = parent.getProperties();
		return cfg.getProperty("server", "yes").equals("yes");
	}

}
