package com.guenego.ocp;

import com.guenego.misc.URL;

public class UnknownChannel extends Channel {

	private URL url;

	public UnknownChannel(URL url) {
		this.url = url;
	}

	@Override
	public String request(String string) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Contact getContact() throws Exception {
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
