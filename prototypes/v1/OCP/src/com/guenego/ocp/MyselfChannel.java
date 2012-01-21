package com.guenego.ocp;

import com.guenego.misc.JLGException;
import com.guenego.misc.URL;

public class MyselfChannel extends Channel {

	private OCPAgent agent;
	

	public MyselfChannel(URL url, OCPAgent agent) {
		this.agent = agent;
		this.url = url;
	}

	public MyselfChannel() {
	}

	@Override
	public byte[] request(byte[] input) throws Exception {
		return (new Protocol(agent).process(new String(input), null)).getBytes();
	}

	@Override
	public OCPContact getContact() throws JLGException {
		throw new JLGException("why do you need to call me ?");
	}

	@Override
	public String toString() {
		if (url == null) {
			return "<url_not_specified>";
		}
		return url.toString();
	}

}
