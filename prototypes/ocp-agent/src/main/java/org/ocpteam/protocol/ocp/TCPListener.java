package org.ocpteam.protocol.ocp;

import org.ocpteam.interfaces.IListener;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.TCPServer;
import org.ocpteam.misc.URL;

public class TCPListener implements IListener {

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
	public void setUrl(URL url) {
		this.url = url;
	}
	
	@Override
	public String toString() {
		return "TCPListener:" + url;
	}

}
