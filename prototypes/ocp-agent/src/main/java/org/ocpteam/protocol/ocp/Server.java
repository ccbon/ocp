package org.ocpteam.protocol.ocp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ocpteam.misc.JLG;
import org.ocpteam.misc.JLGException;
import org.ocpteam.misc.URL;


public class Server {

	
	public OCPAgent agent;
	public List<Listener> listenerList;
	

	public Server(OCPAgent agent) throws JLGException {
		try {
			this.agent = agent;
			
			boolean bFound = false;
			listenerList = new ArrayList<Listener>();
			Iterator<String> it = agent.cfg.stringPropertyNames().iterator();
			while (it.hasNext()) {
				String key = it.next();
				if (key.startsWith("server.listener.")) {
					bFound = true;
					URL url = new URL(agent.cfg.getProperty(key));
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
				listenerList.add(new TCPListener(agent, new URL("tcp://localhost:22222")));
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
