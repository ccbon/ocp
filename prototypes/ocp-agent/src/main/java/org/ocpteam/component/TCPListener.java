package org.ocpteam.component;

import java.net.URI;

import org.ocpteam.interfaces.IListener;
import org.ocpteam.interfaces.IProtocol;
import org.ocpteam.misc.LOG;

public class TCPListener extends DSContainer<DataSource> implements IListener {

	private URI url;

	private IProtocol protocol;

	private TCPServer tcpServer;

	public TCPListener() throws Exception {
		//addComponent(NATTraversal.class);
		addComponent(TCPServer.class);
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
	
		tcpServer = getComponent(TCPServer.class).getClass().newInstance();
		tcpServer.setPort(port);
		tcpServer.setProtocol(protocol);
		tcpServer.start();
	}

	@Override
	public void stop() {
		LOG.info("stopping tcp listener");
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
	public void setProtocol(IProtocol p) {
		this.protocol = p;

	}

}
