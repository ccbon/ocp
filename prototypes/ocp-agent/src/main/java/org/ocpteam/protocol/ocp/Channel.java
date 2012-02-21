package org.ocpteam.protocol.ocp;

import org.ocpteam.misc.URL;

public abstract class Channel {

	protected URL url;
	
	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public static Channel getInstance(URL url, OCPAgent agent) {
		if (url.getProtocol().equalsIgnoreCase("tcp")) {
			return new TCPChannel(url);
		} else if (url.getProtocol().equalsIgnoreCase("myself")) {
			return new MyselfChannel(url, agent);
		} else {
			return new UnknownChannel(url);
		}
	}
	
	public abstract byte[] request(byte[] input) throws Exception;

	public abstract OCPContact getContact() throws Exception;

}
