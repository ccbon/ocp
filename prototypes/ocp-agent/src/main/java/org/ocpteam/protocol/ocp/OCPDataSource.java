package org.ocpteam.protocol.ocp;

import org.ocpteam.component.Agent;
import org.ocpteam.component.Authentication;
import org.ocpteam.component.Client;
import org.ocpteam.component.ContactMap;
import org.ocpteam.component.DSPDataSource;
import org.ocpteam.component.NaivePersistentMap;
import org.ocpteam.component.Protocol;
import org.ocpteam.component.Server;
import org.ocpteam.interfaces.IPersistentMap;

public class OCPDataSource extends DSPDataSource {

	protected OCPAgent agent;
	public ContactMap contactMap;
	public Protocol protocol;

	public OCPDataSource() throws Exception {
		super();
		replaceComponent(Agent.class, new OCPAgent());
		replaceComponent(Client.class, new OCPClient());
		replaceComponent(Server.class, new OCPServer());
		replaceComponent(Protocol.class, new OCPProtocol());
		replaceComponent(ContactMap.class, new OCPContactMap());

		addComponent(Authentication.class, new OCPAuthentication());
		addComponent(IPersistentMap.class, new NaivePersistentMap());
	}

	@Override
	public void init() throws Exception {
		super.init();
		agent = (OCPAgent) getComponent(Agent.class);
		contactMap = getComponent(ContactMap.class);
		protocol = getComponent(Protocol.class);
	}

	@Override
	public String getProtocol() {
		return "OCP";
	}

	@Override
	public void connect() throws Exception {
		agent.readConfig();
		super.connect();
	}

	@Override
	protected void readNetworkConfig() throws Exception {
		super.readNetworkConfig();
		agent.connect();
	}

	@Override
	public void disconnect() throws Exception {
		((OCPAgent) getComponent(Agent.class)).disconnect();
	}

	@Override
	protected void configureServer(Server server) throws Exception {
	}
	
	@Override
	public String getName() {
		if (((OCPAgent) getComponent(Agent.class)).id != null) {
			return ((OCPAgent) getComponent(Agent.class)).id.toString();
		}
		return super.getName();
	}

}
