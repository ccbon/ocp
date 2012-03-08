package org.ocpteam.protocol.sftp;

import org.ocpteam.layer.rsp.Agent;
import org.ocpteam.layer.rsp.Authentication;
import org.ocpteam.layer.rsp.DataSource;

public class SFTPDataSource extends DataSource {

	private Authentication auth;

	
	public SFTPDataSource() {
		auth = new Authentication(this);
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

	@Override
	public Authentication getAuthentication() {
		return this.auth;
	}




}
