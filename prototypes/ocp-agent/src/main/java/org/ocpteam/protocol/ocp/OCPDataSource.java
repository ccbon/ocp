package org.ocpteam.protocol.ocp;

import org.ocpteam.component.Agent;
import org.ocpteam.component.Authentication;
import org.ocpteam.component.ContactMap;
import org.ocpteam.component.NaivePersistentMap;
import org.ocpteam.component.PersistentMap;
import org.ocpteam.component.Server;
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
