package org.ocpteam.component;

import org.ocpteam.interfaces.IAgent;
import org.ocpteam.interfaces.IClient;
import org.ocpteam.interfaces.IServer;

public class Agent extends DataSourceContainer implements IAgent {

	protected IClient client;
	protected Server server;

	@Override
	public IClient getClient() {
		if (client == null && ds().usesComponent(Client.class)) {
			client = ds().getComponent(Client.class);
		}
		return client;
	}

	@Override
	public IServer getServer() {
		if (server == null && ds().usesComponent(Server.class)) {
			server = ds().getComponent(Server.class);
		}
		return server;
	}

	@Override
	public boolean isFirstAgent() {
		return ds().get("agent.isFirst", "yes").equalsIgnoreCase("yes");
	}

}
