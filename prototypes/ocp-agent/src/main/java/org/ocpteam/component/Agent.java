package org.ocpteam.component;

import org.ocpteam.interfaces.IAgent;
import org.ocpteam.interfaces.IClient;
import org.ocpteam.interfaces.IServer;

public class Agent extends DSContainer<DSPDataSource> implements IAgent {

	@Override
	public IClient getClient() {
		return ds().getComponent(Client.class);
	}

	@Override
	public IServer getServer() {
		return ds().getComponent(Server.class);
	}

	@Override
	public boolean isFirstAgent() {
		return ds().getProperty("agent.isFirst", "yes").equalsIgnoreCase("yes");
	}


}
