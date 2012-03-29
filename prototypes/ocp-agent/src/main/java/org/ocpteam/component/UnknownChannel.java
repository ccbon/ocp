package org.ocpteam.component;


public class UnknownChannel extends Channel {

	@Override
	public byte[] request(byte[] input) throws Exception {
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
