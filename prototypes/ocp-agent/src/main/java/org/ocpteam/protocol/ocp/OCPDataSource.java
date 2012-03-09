package org.ocpteam.protocol.ocp;

import java.io.File;

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
		design();
	}

	private void design() throws Exception {
		designer.add(Authentication.class, new OCPAuthentication());
		designer.add(Server.class);
		designer.add(ContactMap.class);
		designer.add(PersistentMap.class, new NaivePersistentMap());
	}
	
	public OCPDataSource(File file) {
		setFile(file);
	}

	@Override
	public String getProtocol() {
		return "OCP";
	}

	@Override
	protected Agent createAgent() {
		return new OCPAgent(this);
	}

}
