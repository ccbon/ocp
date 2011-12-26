package com.guenego.ocp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.guenego.misc.JLG;
import com.guenego.misc.JLGException;
import com.guenego.misc.URL;

public class Server {

	
	public Agent agent;
	public List<Listener> listenerList;
	

	public Server(Agent agent) throws JLGException {
		try {
			this.agent = agent;
			
			listenerList = new ArrayList<Listener>();
			Iterator<String> it = agent.p.stringPropertyNames().iterator();
			while (it.hasNext()) {
				String key = it.next();
				if (key.startsWith("server.listener.")) {
					URL url = new URL(agent.p.getProperty(key));
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
		} catch (Exception e) {
			throw new JLGException(e);
		}
	}

	public void start() throws Exception {
		
		for (Iterator<Listener> it = listenerList.iterator(); it.hasNext();) {
			it.next().start();
		}
	}

	public void stop() {
		JLG.debug("stopping servers");
		for (Iterator<Listener> it = listenerList.iterator(); it.hasNext();) {
			it.next().stop();
		}

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
