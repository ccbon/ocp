package org.ocpteam.component;

import org.ocpteam.protocol.ocp.OCPContact;

public class MyselfChannel extends Channel {
	
	public MyselfChannel() {
	}

	@Override
	public byte[] request(byte[] input) throws Exception {
		return ds().getComponent(Protocol.class).process(input, null);
	}

	@Override
	public OCPContact getContact() throws Exception {
		throw new Exception("why do you need to call me ?");
	}

	@Override
	public String toString() {
		if (url == null) {
			return "<url_not_specified>";
		}
		return url.toString();
	}

	@Override
	public String getProtocolName() {
		return "myself";
	}

}
