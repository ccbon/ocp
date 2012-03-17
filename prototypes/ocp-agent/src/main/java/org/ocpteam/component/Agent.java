package org.ocpteam.component;

import org.ocpteam.core.IComponent;
import org.ocpteam.core.IContainer;

public class Agent implements IComponent, IAgent {

	public DataSource ds;
	private IClient client;
	private IServer server;

	@Override
	public void setParent(IContainer parent) {
		ds = (DataSource) parent;
	}

	@Override
	public IClient getClient() {
		if (client == null && ds.getDesigner().uses(Client.class)) {
			client = ds.getDesigner().get(Client.class);
		}
		return client;
	}

	@Override
	public IServer getServer() {
		if (server == null && ds.getDesigner().uses(Server.class)) {
			server = ds.getDesigner().get(Server.class);
		}
		return server;
	}

}
