package org.ocpteam.protocol.ftp;

import java.net.URI;

import org.ocpteam.functionality.Authentication;
import org.ocpteam.layer.rsp.Agent;
import org.ocpteam.layer.rsp.DataSource;

public class FTPDataSource extends DataSource {

	public FTPDataSource() throws Exception {
		super();
		getDesigner().add(Authentication.class);
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

}
