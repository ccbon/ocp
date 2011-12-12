package com.guenego.ocp;

import com.guenego.misc.Id;
import com.guenego.misc.JLGException;
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
	public Id ping() throws JLGException {
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
