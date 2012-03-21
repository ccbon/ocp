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
		getDesigner().add(Agent.class, new OCPAgent());
		getDesigner().add(Client.class, new OCPClient());
		getDesigner().add(Authentication.class, new OCPAuthentication());
		getDesigner().add(Server.class, new OCPServer());
		getDesigner().add(ContactMap.class);
		getDesigner().add(IPersistentMap.class, new NaivePersistentMap());
		getDesigner().add(IProtocol.class, new Protocol());
	}

	@Override
	public String getProtocol() {
		return "OCP";
	}
	
	@Override
	public void connect() throws Exception {
		((OCPAgent) getDesigner().add(Agent.class)).connect();
	}
	
	@Override
	public void disconnect() throws Exception {
		((OCPAgent) getDesigner().add(Agent.class)).disconnect();
	}

}
