package org.ocpteam.component;

import org.ocpteam.interfaces.IAgent;
import org.ocpteam.interfaces.IClient;
import org.ocpteam.interfaces.IServer;

public class Agent extends DataSourceContainer implements IAgent {

	@Override
	public DSPDataSource ds() {
		// TODO Auto-generated method stub
		return (DSPDataSource) super.ds();
	}
	
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
