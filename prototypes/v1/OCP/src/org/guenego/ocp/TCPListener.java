package org.guenego.ocp;

import org.guenego.misc.JLG;
import org.guenego.misc.TCPServer;
import org.guenego.misc.URL;

public class TCPListener implements Listener {

	public TCPServer tcpServer;
	private URL url;
	private NATTraversal natTraversal;
	
	public TCPListener(OCPAgent agent, URL url) {
		this.url = url;
		tcpServer = new TCPServer(url.getPort(), new TCPServerHandler(agent));
		natTraversal = new NATTraversal(url.getPort());
	}

	@Override
	public void start() {
		tcpServer.start();
		natTraversal.map();
	}

	@Override
	public void stop() {
		JLG.debug("stopping tcp server");
		natTraversal.unmap();
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
