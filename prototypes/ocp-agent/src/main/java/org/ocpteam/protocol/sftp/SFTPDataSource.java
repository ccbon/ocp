package org.ocpteam.protocol.sftp;

import org.ocpteam.layer.rsp.Agent;
import org.ocpteam.layer.rsp.Authentication;
import org.ocpteam.layer.rsp.PropertiesDataSource;

public class SFTPDataSource extends PropertiesDataSource {

	public SFTPDataSource() throws Exception {
		super();
		designer.add(Authentication.class);
	}

	@Override
	public String getProtocol() {
		return "SFTP";
	}

	@Override
	protected Agent createAgent() {
		SFTPAgent a = new SFTPAgent(this);
		return a;
	}

}
