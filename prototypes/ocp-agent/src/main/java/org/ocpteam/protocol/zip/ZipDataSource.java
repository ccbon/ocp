package org.ocpteam.protocol.zip;

import org.ocpteam.layer.rsp.Agent;
import org.ocpteam.layer.rsp.DataSource;

public class ZipDataSource extends DataSource {
	public ZipDataSource() throws Exception {
		super();
		getDesigner().add(Agent.class, new ZipAgent());
	}
	@Override
	public String getProtocol() {
		return "ZIP";
	}
}
