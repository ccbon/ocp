package org.ocpteam.component;

import java.net.ConnectException;

import org.ocpteam.entity.Contact;
import org.ocpteam.entity.InputMessage;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.URL;
import org.ocpteam.module.DSPModule;


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
	public Contact getContact() throws Exception {
		try {
			JLG.debug("getContact");
			DSPModule m = getProtocol().getComponent(DSPModule.class);
			byte[] input = getProtocol().getMessageSerializer().serializeInput(new InputMessage(m.getContact()));
			byte[] response = request(input);
			Contact c = (Contact) getProtocol().getMessageSerializer().deserializeOutput(response);
			// we update a host because an agent does not see its public address.
			c.updateHost(url.getHost());
			return c;
		} catch (ConnectException e) {
			return null;
		}
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
