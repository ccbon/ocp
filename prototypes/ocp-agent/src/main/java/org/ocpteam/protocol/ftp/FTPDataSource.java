package org.ocpteam.protocol.ftp;

import java.net.URI;

import org.ocpteam.layer.rsp.Agent;
import org.ocpteam.layer.rsp.Authentication;
import org.ocpteam.layer.rsp.DataSource;

public class FTPDataSource extends DataSource {

	private Authentication auth;

	public FTPDataSource() {
	}

	public FTPDataSource(URI uri) {
		setURI(uri);
	}

	@Override
	public String getProtocol() {
		return "FTP";
	}

	@Override
	protected Agent createAgent() {
		return new FTPAgent(this);
	}

	@Override
	public Authentication getAuthentication() {
		if (this.auth == null) {
			this.auth = new Authentication(uri);
		}
		return this.auth;
	}

}
