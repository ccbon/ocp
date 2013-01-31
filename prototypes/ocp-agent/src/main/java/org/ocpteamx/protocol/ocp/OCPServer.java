package org.ocpteamx.protocol.ocp;

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;

import org.ocpteam.component.HTTPListener;
import org.ocpteam.component.Protocol;
import org.ocpteam.component.Server;
import org.ocpteam.component.TCPListener;
import org.ocpteam.interfaces.IListener;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.LOG;

public class OCPServer extends Server {

	public OCPAgent agent;

	public void configure() throws Exception {
		LOG.info("ds = " + ds());
		this.agent = ds().getComponent(OCPAgent.class);

		boolean bFound = false;
		listenerList = new ArrayList<IListener>();
		Iterator<String> it = agent.ds().iterator();
		while (it.hasNext()) {
			String key = it.next();
			if (key.startsWith("server.listener.")) {
				bFound = true;
				URI url = new URI(agent.ds().getProperty(key));
				String sProtocol = url.getScheme();
				IListener listener = null;
				if (sProtocol.equalsIgnoreCase("tcp")) {
					listener = ds().addComponent(TCPListener.class);
					listener.init();
					listener.setUrl(url);
				} else if (sProtocol.equalsIgnoreCase("http")) {
					listener = ds().addComponent(HTTPListener.class);
					listener.init();
					listener.setUrl(url);
				} else {
					throw new Exception("protocol not found");
				}
				listener.setProtocol(ds().getComponent(Protocol.class));
				listenerList.add(listener);
			}

		}
		if (bFound == false) {
			IListener l = ds().addComponent(TCPListener.class);
			l.setUrl(new URI("tcp://localhost:22222"));
			l.setProtocol(ds().getComponent(Protocol.class));
			listenerList.add(l);
		}
	}

	@Override
	public void start() throws Exception {
		configure();
		for (Iterator<IListener> it = listenerList.iterator(); it.hasNext();) {
			it.next().start();
		}
		bIsStarted = true;
	}

	@Override
	public void stop() {
		LOG.info("stopping servers");
		for (Iterator<IListener> it = listenerList.iterator(); it.hasNext();) {
			it.next().stop();
		}
		bIsStarted = false;
		LOG.info("servers stopped.");
	}

	@Override
	public String toString() {
		String result = "";
		for (Iterator<IListener> it = listenerList.iterator(); it.hasNext();) {
			IListener l = it.next();
			result += "listener=" + l + JLG.NL;
		}
		return result;
	}

}
