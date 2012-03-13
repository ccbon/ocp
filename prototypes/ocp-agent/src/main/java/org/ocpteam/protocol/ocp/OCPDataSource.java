package org.ocpteam.protocol.ocp;

import org.ocpteam.functionality.Authentication;
import org.ocpteam.functionality.ContactMap;
import org.ocpteam.functionality.NaivePersistentMap;
import org.ocpteam.functionality.PersistentMap;
import org.ocpteam.functionality.Server;
import org.ocpteam.layer.rsp.Agent;
import org.ocpteam.layer.rsp.PropertiesDataSource;

public class OCPDataSource extends PropertiesDataSource {

	public OCPDataSource() throws Exception {
		super();
		getDesigner().add(Agent.class, new OCPAgent());
		getDesigner().add(Authentication.class, new OCPAuthentication());
		getDesigner().add(Server.class);
		getDesigner().add(ContactMap.class);
		getDesigner().add(PersistentMap.class, new NaivePersistentMap());
	}

	@Override
	public String getProtocol() {
		return "OCP";
	}

}
