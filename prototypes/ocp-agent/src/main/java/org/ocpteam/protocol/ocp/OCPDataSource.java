package org.ocpteam.protocol.ocp;

import java.io.File;

import org.ocpteam.layer.rsp.Agent;
import org.ocpteam.layer.rsp.Authentication;
import org.ocpteam.layer.rsp.PropertiesDataSource;
import org.ocpteam.layer.rsp.Server;

public class OCPDataSource extends PropertiesDataSource {

	public OCPDataSource() throws Exception {
		super();
		design();
	}

	private void design() throws Exception {
		designer.add(Authentication.class, new OCPAuthentication());
		designer.add(Server.class);
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
