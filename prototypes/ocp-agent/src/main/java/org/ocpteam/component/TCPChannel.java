package org.ocpteam.component;

import java.net.ConnectException;

import org.ocpteam.entity.Contact;
import org.ocpteam.entity.InputMessage;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.TCPClient;
import org.ocpteam.misc.URL;
import org.ocpteam.module.DSPModule;


public class TCPChannel extends Channel {

	private TCPClient tcpClient;

	@Override
	public void setUrl(URL url) {
		this.url = url;
		int port = url.getPort();
		if (port == -1) {
			port = url.getDefaultPort();
		}
		String hostname = url.getHost();
		tcpClient = new TCPClient(hostname, port);
	}
	
	@Override
	public byte[] request(byte[] input) throws Exception {
		return tcpClient.request(input);
	}

	@Override
	public Contact getContact() throws Exception {
		try {
			JLG.debug("getContact");
			DSPModule m = getProtocol().getComponent(DSPModule.class);
			byte[] input = getProtocol().getMessageSerializer().serializeInput(new InputMessage(m.getContact));
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
