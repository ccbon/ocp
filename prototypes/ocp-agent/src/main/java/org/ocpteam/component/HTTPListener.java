package org.ocpteam.component;

import org.ocpteam.interfaces.IListener;
import org.ocpteam.interfaces.IProtocol;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.URL;

import com.sun.net.httpserver.HttpServer;

@SuppressWarnings("restriction")
public class HTTPListener extends DataSourceContainer implements IListener {

	private URL url;
	HttpServer oldserver;
	private HTTPServer httpServer;
	private HTTPServerHandler handler;
	protected IProtocol protocol;
	private Thread t;
	
	public HTTPListener() throws Exception {
		addComponent(NATTraversal.class);
		addComponent(HTTPServer.class);
		addComponent(HTTPServerHandler.class);
	}
	
	@Override
	public void init() throws Exception {
		super.init();
		httpServer = getComponent(HTTPServer.class);
		handler = getComponent(HTTPServerHandler.class);
	}
	
	@Override
	public void start() {
		try {
			
			int port = url.getPort();
			if (usesComponent(NATTraversal.class)) {
				getComponent(NATTraversal.class).setPort(port);
				getComponent(NATTraversal.class).map();
			}
			
			httpServer.setPort(port);
			httpServer.setHandler(handler);

			t = new Thread(httpServer, "HTTPServer");
			t.start();

			
		} catch (Exception e) {
			JLG.error(e);
		}
	}

	@Override
	public void stop() {
		JLG.debug("stopping http server");
		if (usesComponent(NATTraversal.class)) {
			getComponent(NATTraversal.class).unmap();
		}
		httpServer.stop(t);
		

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
	public void setProtocol(IProtocol p) {
		this.protocol = p;
	}

}
