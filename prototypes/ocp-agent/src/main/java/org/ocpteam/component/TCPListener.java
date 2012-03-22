package org.ocpteam.component;

import org.ocpteam.core.IContainer;
import org.ocpteam.interfaces.IListener;
import org.ocpteam.interfaces.IProtocol;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.URL;

public class TCPListener extends DataSourceContainer implements IListener {

	private URL url;
	
	private IProtocol protocol;
		
	protected IContainer parent;
	private Thread t;
	
	
	public TCPListener() throws Exception {
		addComponent(NATTraversal.class);
		addComponent(TCPServer.class);
		addComponent(TCPServerHandler.class);
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
	public void start() {
		int port = url.getPort();
		if (usesComponent(NATTraversal.class)) {
			getComponent(NATTraversal.class).setPort(port);
			getComponent(NATTraversal.class).map();
		}
		
		getComponent(TCPServer.class).setPort(port);
		getComponent(TCPServer.class).setHandler(getComponent(TCPServerHandler.class));
		getComponent(TCPServer.class).setProtocol(protocol);

		t = new Thread(getComponent(TCPServer.class));
		t.start();
	}

	@Override
	public void stop() {
		JLG.debug("stopping tcp listener");
		if (usesComponent(NATTraversal.class)) {
			getComponent(NATTraversal.class).unmap();
		}
		getComponent(TCPServer.class).stop(t);
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

	@Override
	public void setProtocol(IProtocol p) {
		this.protocol = p;
		
	}

}
