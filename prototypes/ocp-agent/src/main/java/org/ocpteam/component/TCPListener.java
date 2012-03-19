package org.ocpteam.component;

import org.ocpteam.core.Container;
import org.ocpteam.core.IComponent;
import org.ocpteam.core.IContainer;
import org.ocpteam.interfaces.IListener;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.TCPServer;
import org.ocpteam.misc.URL;
import org.ocpteam.protocol.ocp.TCPServerHandler;

public class TCPListener extends Container implements IComponent, IListener {

	public TCPServer tcpServer;
	private URL url;
	private NATTraversal natTraversal;
	private IContainer parent;
	
	public TCPListener() throws Exception {
		getDesigner().add(NATTraversal.class);
	}

	@Override
	public void start() {
		int port = url.getPort();
		JLG.debug("parent = " + parent);
		Agent agent = parent.getDesigner().get(Agent.class);
		
		tcpServer = new TCPServer(port, new TCPServerHandler(agent));
		natTraversal = getDesigner().get(NATTraversal.class);
		natTraversal.setPort(url.getPort());

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

	@Override
	public void setParent(IContainer parent) {
		this.parent = parent;
		
	}

}
