package org.ocpteam.component;

import org.ocpteam.core.Container;
import org.ocpteam.core.IComponent;
import org.ocpteam.core.IContainer;
import org.ocpteam.interfaces.IListener;
import org.ocpteam.interfaces.ITCPServerHandler;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.URL;
import org.ocpteam.protocol.ocp.TCPServerHandler;

public class TCPListener extends Container implements IComponent, IListener {

	public TCPServer tcpServer;
	private URL url;
	private NATTraversal natTraversal;
	protected IContainer parent;
	private Thread t;
	
	public TCPListener() throws Exception {
		getDesigner().add(NATTraversal.class);
		getDesigner().add(TCPServer.class);
		getDesigner().add(TCPServerHandler.class);
	}

	@Override
	public void start() {
		int port = url.getPort();
		
		tcpServer = getDesigner().get(TCPServer.class);
		tcpServer.setPort(port);
		ITCPServerHandler handler = getDesigner().get(TCPServerHandler.class);
		tcpServer.setHandler(handler);
		natTraversal = getDesigner().get(NATTraversal.class);
		natTraversal.setPort(port);

		t = new Thread(tcpServer);
		t.start();
		natTraversal.map();
	}

	@Override
	public void stop() {
		JLG.debug("stopping tcp server");
		natTraversal.unmap();
		tcpServer.stop(t);
		
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
	
	@Override
	public IContainer getParent() {
		return parent;
	}

}
