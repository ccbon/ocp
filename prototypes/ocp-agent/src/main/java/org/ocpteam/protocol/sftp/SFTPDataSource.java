package org.ocpteam.protocol.sftp;

import java.net.URI;

import org.ocpteam.layer.rsp.Agent;
import org.ocpteam.layer.rsp.Authentication;
import org.ocpteam.layer.rsp.DataSource;

public class SFTPDataSource extends DataSource {

	private Authentication auth;

	
	public SFTPDataSource() {
	}
	
	public SFTPDataSource(URI uri, SSHChallenge c) {
		setURI(uri);
		auth = new Authentication(this);
		auth.setChallenge(c);
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
