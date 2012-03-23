package org.ocpteam.protocol.ocp;

import org.ocpteam.component.Agent;
import org.ocpteam.component.Authentication;
import org.ocpteam.component.Client;
import org.ocpteam.component.ContactMap;
import org.ocpteam.component.DataSource;
import org.ocpteam.component.NaivePersistentMap;
import org.ocpteam.component.Protocol;
import org.ocpteam.component.Server;
import org.ocpteam.interfaces.IPersistentMap;

public class OCPDataSource extends DataSource {

	public ContactMap contactMap;
	public Protocol protocol;

	public OCPDataSource() throws Exception {
		super();
		addComponent(Agent.class, new OCPAgent());
		addComponent(Client.class, new OCPClient());
		addComponent(Authentication.class, new OCPAuthentication());
		addComponent(Server.class, new OCPServer());
		addComponent(ContactMap.class);
		addComponent(IPersistentMap.class, new NaivePersistentMap());
		addComponent(Protocol.class, new OCPProtocol());
	}
	
	@Override
	public void init() {
		super.init();
		contactMap = getComponent(ContactMap.class);
		protocol = getComponent(Protocol.class);
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
