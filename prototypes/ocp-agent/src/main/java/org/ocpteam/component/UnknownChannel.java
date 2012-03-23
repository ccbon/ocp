package org.ocpteam.component;

import org.ocpteam.protocol.ocp.OCPContact;

public class UnknownChannel extends Channel {

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

	@Override
	public String getProtocolName() {
		return "unknown";
	}
}
