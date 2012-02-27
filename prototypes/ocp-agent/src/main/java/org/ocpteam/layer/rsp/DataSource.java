package org.ocpteam.layer.rsp;

import java.net.URI;
import java.util.ResourceBundle;

public class DataSource {

	public static ResourceBundle protocolResource = ResourceBundle
			.getBundle("protocols");

	private URI uri;

	public DataSource(URI uri) {
		this.uri = uri;
	}

	public static DataSource getInstance(URI uri) {
		return new DataSource(uri);
	}

	public Agent getAgent() throws Exception {
		String scheme = uri.getScheme().toUpperCase();
		String agentClassString = protocolResource.getString(scheme);
		return (Agent) Class.forName(agentClassString).newInstance();
	}
}
