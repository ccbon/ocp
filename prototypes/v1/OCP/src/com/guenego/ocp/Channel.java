package com.guenego.ocp;

import com.guenego.misc.URL;

public abstract class Channel {

	protected URL url;
	
	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public static Channel getInstance(URL url, Agent agent) {
		if (url.getProtocol().equalsIgnoreCase("tcp")) {
			return new TCPChannel(url);
		} else if (url.getProtocol().equalsIgnoreCase("myself")) {
			return new MyselfChannel(url, agent);
		} else {
			return new UnknownChannel(url);
		}
	}
	
	public abstract String request(String string) throws Exception;

	public abstract Contact getContact() throws Exception;

}
