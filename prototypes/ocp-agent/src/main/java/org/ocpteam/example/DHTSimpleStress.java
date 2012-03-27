package org.ocpteam.example;

import java.util.Properties;

import org.ocpteam.component.NATTraversal;
import org.ocpteam.component.TCPServer;
import org.ocpteam.core.TopContainer;
import org.ocpteam.misc.JLG;
import org.ocpteam.protocol.dht.DHTDataModel;
import org.ocpteam.protocol.dht.DHTDataSource;

public class DHTSimpleStress extends TopContainer {

	public static void main(String[] args) {
		try {
			DHTSimpleStress app = new DHTSimpleStress();
			app.init();
			app.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private int n;
	private int port;

	public DHTSimpleStress() throws Exception {
		addComponent(DHTDataSource.class);
	}

	@Override
	public void init() throws Exception {
		super.init();
		n = 10;
		port = 40000;
		JLG.debug_on();
		JLG.bUseSet = true;
		JLG.set.add(TCPServer.class.getName());
		JLG.set.add(DHTSimpleStress.class.getName());
		JLG.set.add(NATTraversal.class.getName());
	}

	public void start() throws Exception {
		DHTDataSource[] ds = new DHTDataSource[n];
		for (int i = 0; i < n; i++) {
			Class<? extends DHTDataSource> c = getComponent(DHTDataSource.class)
					.getClass();
			ds[i] = c.newInstance();
			ds[i].init();
			//unfortunately, the teleal library does not work well with many threads...
			ds[i].listener.removeComponent(NATTraversal.class);
		}
		// first agent
		Properties p = new Properties();
		p.setProperty("agent.isFirst", "yes");
		p.setProperty("listener.tcp.url", "tcp://localhost:" + port);
		ds[0].setConfig(p);
		ds[0].connect();

		// other agents
		for (int i = 1; i < n; i++) {
			p = new Properties();
			p.setProperty("agent.isFirst", "no");
			int port_i = port + i;
			p.setProperty("listener.tcp.url", "tcp://localhost:" + port_i);
			p.setProperty("sponsor.1", "tcp://localhost:" + port);
			ds[i].setConfig(p);
			ds[i].connect();
		}
		
		for (int i = 0; i < n; i++) {
			DHTDataModel dht = (DHTDataModel) ds[i].getContext().getDataModel();
			dht.set("key" + i, "value" + i);
		}
		
		for (int i = 0; i < n; i++) {
			DHTDataModel dht = (DHTDataModel) ds[i].getContext().getDataModel();
			JLG.debug(dht.keySet().toString());
			int j = 9 - i;
			JLG.debug("key" + j + " = " + dht.get("key" + j));
		}
		
		for (int i = 0; i < n; i++) {
			ds[i].disconnect();
		}
		JLG.debug("app finished");
	}

}
