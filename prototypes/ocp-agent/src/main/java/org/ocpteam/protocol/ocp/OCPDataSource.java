package org.ocpteam.protocol.ocp;

import org.ocpteam.component.Agent;
import org.ocpteam.component.Authentication;
import org.ocpteam.component.Client;
import org.ocpteam.component.ContactMap;
import org.ocpteam.component.DataSource;
import org.ocpteam.component.IPersistentMap;
import org.ocpteam.component.NaivePersistentMap;
import org.ocpteam.component.Server;

public class OCPDataSource extends DataSource {

	public OCPDataSource() throws Exception {
		super();
		getDesigner().add(Agent.class, new OCPAgent());
		getDesigner().add(Client.class, new OCPClient());
		getDesigner().add(Authentication.class, new OCPAuthentication());
		getDesigner().add(Server.class);
		getDesigner().add(ContactMap.class);
		getDesigner().add(IPersistentMap.class, new NaivePersistentMap());
	}

	@Override
	public String getProtocol() {
		return "OCP";
	}

}
