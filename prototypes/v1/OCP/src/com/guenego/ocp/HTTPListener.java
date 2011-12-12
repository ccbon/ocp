package com.guenego.ocp;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import com.guenego.misc.JLG;
import com.guenego.misc.URL;
import com.sun.net.httpserver.HttpServer;

public class HTTPListener implements Listener {

	private URL url;
	private Agent agent;
	HttpServer server;

	public HTTPListener(Agent agent, URL url) {
		this.url = url;
		this.agent = agent;
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
		} catch (Exception e) {
			JLG.error(e);
		}
	}

	@Override
	public void stop() {
		JLG.debug("stopping http server");
		server.stop(0);

	}

	@Override
	public URL getUrl() {
		return url;
	}

}
