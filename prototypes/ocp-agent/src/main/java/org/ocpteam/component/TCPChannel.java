package org.ocpteam.component;

import org.ocpteam.misc.URL;


public class TCPChannel extends Channel {

	private TCPClient tcpClient;
	
	public TCPChannel() throws Exception {
		addComponent(TCPClient.class);
	}
	
	@Override
	public void init() throws Exception {
		tcpClient = getComponent(TCPClient.class);
	}

	@Override
	public void setUrl(URL url) {
		super.setUrl(url);
		int port = url.getPort();
		if (port == -1) {
			port = url.getDefaultPort();
		}
		String hostname = url.getHost();
		tcpClient.setPort(port);
		tcpClient.setHostname(hostname);
		tcpClient.setProtocol(getProtocol());
	}
	
	@Override
	public byte[] request(byte[] input) throws Exception {
		byte[] result = tcpClient.request(input);
		if (result == null || result.length == 0) {
			return null;
		}
		return result;
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
		return "tcp";
	}

}
