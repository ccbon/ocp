package com.guenego.ocp;

import com.guenego.misc.JLG;
import com.guenego.misc.TCPServer;
import com.guenego.misc.URL;

public class TCPListener implements Listener {

	public TCPServer tcpServer;
	private URL url;
	
	public TCPListener(Agent agent, URL url) {
		this.url = url;
		tcpServer = new TCPServer(url.getPort(), new TCPServerHandler(agent));
	}

	@Override
	public void start() {
		tcpServer.start();
	}

	@Override
	public void stop() {
		JLG.debug("stopping tcp server");
		tcpServer.stopnow();
		
	}

	@Override
	public URL getUrl() {
		return url;
	}
	
	@Override
	public String toString() {
		return "TCPListener:" + url;
	}

}
