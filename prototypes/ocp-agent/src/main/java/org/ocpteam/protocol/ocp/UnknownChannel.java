package org.ocpteam.protocol.ocp;

import org.ocpteam.misc.URL;

public class UnknownChannel extends Channel {

	private URL url;

	public UnknownChannel(URL url) {
		this.url = url;
	}

	@Override
	public byte[] request(byte[] input) throws Exception {
		return null;
	}

	@Override
	public OCPContact getContact() throws Exception {
		return null;
	}
	
	@Override
	public String toString() {
		if (url == null) {
			return "<url_not_specified>";
		}
		return url.toString();
	}


}
