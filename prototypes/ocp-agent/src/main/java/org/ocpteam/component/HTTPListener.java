package org.ocpteam.component;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.ocpteam.interfaces.IListener;
import org.ocpteam.interfaces.IProtocol;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.URL;
import org.ocpteam.protocol.ocp.HTTPServerHandler;

import com.sun.net.httpserver.HttpServer;

@SuppressWarnings("restriction")
public class HTTPListener extends DataSourceContainer implements IListener {

	private URL url;
	private Agent agent;
	HttpServer server;
	private NATTraversal natTraversal;
	protected IProtocol protocol;

	@Override
	public void init() throws Exception {
		// TODO Auto-generated method stub
		super.init();
		agent = ds().getComponent(Agent.class);
	}
	
	@Override
	public void start() {
		try {
			
			natTraversal = new NATTraversal();
			natTraversal.setPort(url.getPort());
			
			
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

	@Override
	public void setUrl(URL url) {
		this.url = url;
	}

	@Override
	public void setProtocol(IProtocol p) {
		this.protocol = p;
	}

}
