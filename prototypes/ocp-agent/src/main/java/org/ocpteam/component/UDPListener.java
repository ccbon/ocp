package org.ocpteam.component;

import java.net.URI;

import org.ocpteam.interfaces.IListener;
import org.ocpteam.interfaces.IProtocol;
import org.ocpteam.misc.JLG;

public class UDPListener extends DataSourceContainer implements IListener {

	private URI url;

	private IProtocol protocol;

	private UDPServer udpServer;

	private UDPServerHandler handler;

	public UDPListener() throws Exception {
		//addComponent(NATTraversal.class);
		addComponent(UDPServer.class);
		addComponent(UDPServerHandler.class);
	}

	@Override
	public void init() throws Exception {
		super.init();
		handler = getComponent(UDPServerHandler.class);
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
			getComponent(NATTraversal.class).setProtocol("UDP");
			getComponent(NATTraversal.class).map();
		}

		handler.setProtocol(protocol);
		udpServer = getComponent(UDPServer.class).getClass().newInstance();
		udpServer.init();
		udpServer.setPort(port);
		udpServer.setHandler(handler);
		udpServer.start();
	}

	@Override
	public void stop() {
		JLG.debug("stopping tcp listener");
		if (usesComponent(NATTraversal.class)) {
			getComponent(NATTraversal.class).unmap();
		}
		if (udpServer != null) {
			udpServer.stop();
			udpServer = null;
		}
	}

	@Override
	public String toString() {
		return "UDPListener:" + url;
	}

	@Override
	public void setProtocol(IProtocol p) {
		this.protocol = p;
	}

}