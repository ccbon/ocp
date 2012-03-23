package org.ocpteam.component;

import org.ocpteam.entity.Contact;
import org.ocpteam.misc.URL;

/**
 * A Channel object represent the connection created by the client to talk
 * to a contact on the distributed network. It is a wrapper on the contact url.
 * 
 *
 */
public abstract class Channel extends DataSourceContainer {

	protected URL url;
	
	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}
	
	public abstract byte[] request(byte[] input) throws Exception;

	public abstract Contact getContact() throws Exception;

	public abstract String getProtocolName();

}
