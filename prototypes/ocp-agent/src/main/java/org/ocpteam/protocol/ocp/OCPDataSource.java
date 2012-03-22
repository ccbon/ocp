package org.ocpteam.protocol.ocp;

import org.ocpteam.component.Agent;
import org.ocpteam.component.Authentication;
import org.ocpteam.component.Client;
import org.ocpteam.component.ContactMap;
import org.ocpteam.component.DataSource;
import org.ocpteam.component.NaivePersistentMap;
import org.ocpteam.component.Server;
import org.ocpteam.interfaces.IPersistentMap;
import org.ocpteam.interfaces.IProtocol;

public class OCPDataSource extends DataSource {

	public OCPDataSource() throws Exception {
		super();
		addComponent(Agent.class, new OCPAgent());
		addComponent(Client.class, new OCPClient());
		addComponent(Authentication.class, new OCPAuthentication());
		addComponent(Server.class, new OCPServer());
		addComponent(ContactMap.class);
		addComponent(IPersistentMap.class, new NaivePersistentMap());
		addComponent(IProtocol.class, new OCPProtocol());
	}

	@Override
	public String getProtocol() {
		return "OCP";
	}
	
	@Override
	public void connect() throws Exception {
		((OCPAgent) getComponent(Agent.class)).connect();
	}
	
	@Override
	public void disconnect() throws Exception {
		((OCPAgent) getComponent(Agent.class)).disconnect();
	}

}
