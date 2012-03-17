package org.ocpteam.protocol.ocp;

import java.util.ArrayList;
import java.util.Iterator;

import org.ocpteam.component.Agent;
import org.ocpteam.component.Server;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.JLGException;
import org.ocpteam.misc.URL;

public class OCPServer extends Server {

	public OCPAgent agent;
	

	public void init() throws Exception {
		JLG.debug("ds = " + ds);
		JLG.debug("ds.getDesigner() = " + ds.getDesigner());
		this.agent = (OCPAgent) ds.getDesigner().get(Agent.class);

		boolean bFound = false;
		listenerList = new ArrayList<Listener>();
		Iterator<String> it = agent.ds.iterator();
		while (it.hasNext()) {
			String key = it.next();
			if (key.startsWith("server.listener.")) {
				bFound = true;
				URL url = new URL(agent.ds.get(key));
				String sProtocol = url.getProtocol();
				Listener listener = null;
				if (sProtocol.equalsIgnoreCase("tcp")) {
					listener = new TCPListener(agent, url);
				} else if (sProtocol.equalsIgnoreCase("http")) {
					listener = new HTTPListener(agent, url);
				} else {
					throw new JLGException("protocol not found");
				}
				listenerList.add(listener);
			}

		}
		if (bFound == false) {
			listenerList.add(new TCPListener(agent, new URL(
					"tcp://localhost:22222")));
		}
	}

	@Override
	public void start() throws Exception {
		init();
		for (Iterator<Listener> it = listenerList.iterator(); it.hasNext();) {
			it.next().start();
		}
		bIsStarted = true;
	}

	@Override
	public void stop() {
		JLG.debug("stopping servers");
		for (Iterator<Listener> it = listenerList.iterator(); it.hasNext();) {
			it.next().stop();
		}
		bIsStarted = false;
		JLG.debug("servers stopped.");
	}

	@Override
	public String toString() {
		String result = "";
		for (Iterator<Listener> it = listenerList.iterator(); it.hasNext();) {
			Listener l = it.next();
			result += "listener=" + l + JLG.NL;
		}
		return result;
	}

}
