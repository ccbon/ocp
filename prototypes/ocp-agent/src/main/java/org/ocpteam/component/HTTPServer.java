package org.ocpteam.component;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.ocpteam.misc.JLG;

import com.sun.net.httpserver.HttpServer;

@SuppressWarnings("restriction")
public class HTTPServer implements Runnable {

	private int port;
	private HTTPServerHandler handler;
	private HttpServer server;

	@Override
	public void run() {
		try {
			InetSocketAddress addr = new InetSocketAddress(port);
			server = HttpServer.create(addr, 0);

			server.createContext("/", handler);
			server.setExecutor(Executors.newCachedThreadPool());
			server.start();
			JLG.debug("Server is listening on port " + port);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setHandler(HTTPServerHandler handler) {
		this.handler = handler;
	}

	public void stop(Thread t) {
		server.stop(0);
		t.interrupt();
	}

}
