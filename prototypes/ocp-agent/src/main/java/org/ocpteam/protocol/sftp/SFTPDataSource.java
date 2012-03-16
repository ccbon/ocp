package org.ocpteam.protocol.sftp;

import org.ocpteam.component.Agent;
import org.ocpteam.component.Authentication;
import org.ocpteam.layer.rsp.PropertiesDataSource;

public class SFTPDataSource extends PropertiesDataSource {

	public SFTPDataSource() throws Exception {
		super();
		getDesigner().add(Agent.class, new SFTPAgent());
		getDesigner().add(Authentication.class);
	}

	@Override
	public String getProtocol() {
		return "SFTP";
	}

}
