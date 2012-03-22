package org.ocpteam.component;

import org.ocpteam.misc.URL;
import org.ocpteam.protocol.ocp.OCPContact;

public abstract class Channel extends DataSourceContainer {

	protected URL url;
	
	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}
	
	public abstract byte[] request(byte[] input) throws Exception;

	public abstract OCPContact getContact() throws Exception;

	public abstract String getProtocolName();

}
