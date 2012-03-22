package org.ocpteam.protocol.ocp;

import java.util.ArrayList;
import java.util.Iterator;

import org.ocpteam.component.Agent;
import org.ocpteam.component.Server;
import org.ocpteam.component.TCPListener;
import org.ocpteam.interfaces.IListener;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.JLGException;
import org.ocpteam.misc.URL;

public class OCPServer extends Server {

	public OCPAgent agent;

	public void configure() throws Exception {
		JLG.debug("ds = " + ds());
		this.agent = (OCPAgent) ds().getComponent(Agent.class);

		boolean bFound = false;
		listenerList = new ArrayList<IListener>();
		Iterator<String> it = agent.ds().iterator();
		while (it.hasNext()) {
			String key = it.next();
			if (key.startsWith("server.listener.")) {
				bFound = true;
				URL url = new URL(agent.ds().get(key));
				String sProtocol = url.getProtocol();
				IListener listener = null;
				if (sProtocol.equalsIgnoreCase("tcp")) {
					listener = ds().addComponent(TCPListener.class);
					listener.setUrl(url);
				} else if (sProtocol.equalsIgnoreCase("http")) {
					listener = ds().addComponent(HTTPListener.class);
					listener.setUrl(url);
				} else {
					throw new JLGException("protocol not found");
				}
				listenerList.add(listener);
			}

		}
		if (bFound == false) {
			IListener l = ds().addComponent(TCPListener.class);
			l.setUrl(new URL("tcp://localhost:22222"));
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
		JLG.debug("stopping servers");
		for (Iterator<IListener> it = listenerList.iterator(); it.hasNext();) {
			it.next().stop();
		}
		bIsStarted = false;
		JLG.debug("servers stopped.");
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
