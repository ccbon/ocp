package org.ocpteam.component;


public class MyselfChannel extends Channel {
	
	public MyselfChannel() {
	}

	@Override
	public byte[] request(byte[] input) throws Exception {
		return ds().getComponent(Protocol.class).process(input, null);
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
