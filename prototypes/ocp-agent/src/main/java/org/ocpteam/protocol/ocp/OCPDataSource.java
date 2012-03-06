package org.ocpteam.protocol.ocp;

import java.io.File;

import org.ocpteam.layer.rsp.Agent;
import org.ocpteam.layer.rsp.Authentication;
import org.ocpteam.layer.rsp.DataSource;

public class OCPDataSource extends DataSource {

	private Authentication auth;

	public OCPDataSource() {
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

	@Override
	public Authentication getAuthentication() {
		if (this.auth == null) {
			this.auth = new OCPAuthentication(uri);
		}
		return this.auth;
	}





}
