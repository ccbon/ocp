package org.guenego.ocp;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.guenego.misc.JLG;
import org.guenego.misc.URL;

import com.sun.net.httpserver.HttpServer;

public class HTTPListener implements Listener {

	private URL url;
	private OCPAgent agent;
	HttpServer server;
	private NATTraversal natTraversal;

	public HTTPListener(OCPAgent agent, URL url) {
		this.url = url;
		this.agent = agent;
		natTraversal = new NATTraversal(url.getPort());
	}

	@Override
	public void start() {
		try {
			InetSocketAddress addr = new InetSocketAddress(url.getPort());
			server = HttpServer.create(addr, 0);

			server.createContext("/", new HTTPServerHandler(agent));
			server.setExecutor(Executors.newCachedThreadPool());
			server.start();
			JLG.debug("Server is listening on port " + url.getPort());
			natTraversal.map();
		} catch (Exception e) {
			JLG.error(e);
		}
	}

	@Override
	public void stop() {
		JLG.debug("stopping http server");
		natTraversal.unmap();
		server.stop(0);

	}

	@Override
	public URL getUrl() {
		return url;
	}

}
