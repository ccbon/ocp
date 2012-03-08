package org.ocpteam.protocol.zip;

import org.ocpteam.layer.rsp.Agent;
import org.ocpteam.layer.rsp.DataSource;

public class ZipDataSource extends DataSource {

	@Override
	public String getProtocol() {
		return "ZIP";
	}

	@Override
	protected Agent createAgent() {
		return new ZipAgent(this);
	}

	@Override
	public void save() throws Exception {
		// do nothing
	}

}
