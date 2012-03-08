package org.ocpteam.protocol.sftp;

import org.ocpteam.layer.rsp.Agent;
import org.ocpteam.layer.rsp.Authentication;
import org.ocpteam.layer.rsp.DataSource;

public class SFTPDataSource extends DataSource {

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
