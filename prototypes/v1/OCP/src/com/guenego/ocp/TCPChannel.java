package com.guenego.ocp;

import java.net.ConnectException;

import com.guenego.misc.Id;
import com.guenego.misc.JLG;
import com.guenego.misc.JLGException;
import com.guenego.misc.TCPClient;
import com.guenego.misc.URL;

public class TCPChannel extends Channel {

	private TCPClient tcpClient;
	private URL url;

	public TCPChannel(URL url) {
		this.url = url;
		int port = url.getPort();
		if (port == -1) {
			port = url.getDefaultPort();
		}
		String hostname = url.getHost();
		tcpClient = new TCPClient(hostname, port);
	}

	public TCPChannel() {
	}

	@Override
	public String request(String string) throws Exception {
		return tcpClient.request(string);
	}

	@Override
	public Id ping() throws JLGException {
		try {
			JLG.debug("tcp ping");
			String response = request(Protocol.PING);
			Id id = new Id(response);
			return id;
		} catch (ConnectException e) {
			return null;
		} catch (Exception e) {
			throw new JLGException(e);
		}
	}

	@Override
	public String toString() {
		if (url == null) {
			return "<url_not_specified>";
		}
		return url.toString();
	}

}
