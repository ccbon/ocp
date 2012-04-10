package org.ocpteam.component;

import java.net.URI;

import org.ocpteam.core.IContainer;
import org.ocpteam.interfaces.IListener;
import org.ocpteam.interfaces.IProtocol;
import org.ocpteam.misc.JLG;

public class TCPListener extends DataSourceContainer implements IListener {

	private URI url;

	private IProtocol protocol;

	protected IContainer parent;

	private TCPServer tcpServer;

	private TCPServerHandler handler;

	public TCPListener() throws Exception {
		//addComponent(NATTraversal.class);
		addComponent(TCPServer.class);
		addComponent(TCPServerHandler.class);
	}

	@Override
	public void init() throws Exception {
		super.init();
		handler = getComponent(TCPServerHandler.class);
	}

	@Override
	public URI getUrl() {
		return url;
	}

	@Override
	public void setUrl(URI url) {
		this.url = url;
	}

	@Override
	public void start() throws Exception {
		int port = url.getPort();
		if (usesComponent(NATTraversal.class)) {
			getComponent(NATTraversal.class).setPort(port);
			getComponent(NATTraversal.class).map();
		}

		handler.setProtocol(protocol);
		tcpServer = getComponent(TCPServer.class).getClass().newInstance();
		tcpServer.init();
		tcpServer.setPort(port);
		tcpServer.setHandler(handler);
		tcpServer.start();
	}

	@Override
	public void stop() {
		JLG.debug("stopping tcp listener");
		if (usesComponent(NATTraversal.class)) {
			getComponent(NATTraversal.class).unmap();
		}
		if (tcpServer != null) {
			tcpServer.stop();
			tcpServer = null;
		}
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
