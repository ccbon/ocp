package org.ocpteam.component;

import org.ocpteam.core.IComponent;
import org.ocpteam.core.IContainer;
import org.ocpteam.interfaces.IAgent;
import org.ocpteam.interfaces.IClient;
import org.ocpteam.interfaces.IServer;

public class Agent implements IComponent, IAgent {

	public DataSource ds;
	protected IClient client;
	protected Server server;

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

	@Override
	public boolean isFirstAgent() {
		return ds.get("agent.isFirst", "yes").equalsIgnoreCase("yes");
	}

}
