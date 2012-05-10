package org.ocpteam.example;

import java.util.Properties;

import org.ocpteam.component.ContactMap;
import org.ocpteam.component.NATTraversal;
import org.ocpteam.core.TopContainer;
import org.ocpteam.misc.JLG;
import org.ocpteamx.protocol.dht0.DHTDataSource;

public class ThreadStress extends TopContainer {

	public static void main(String[] args) {
		try {
			ThreadStress app = new ThreadStress();
			app.init();
			app.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private int n;
	private int port;
	private DHTDataSource[] ds;

	public ThreadStress() throws Exception {
		addComponent(DHTDataSource.class);
	}

	@Override
	public void init() throws Exception {
		super.init();
		n = 2;
		port = 40000;
		JLG.debug_on();
	}

	public void start() throws Exception {

		connectAll();
		ds[1].disconnect();
		ds[1].connect();
		disconnectAll();
		JLG.debug("app finished");
		JLG.showActiveThreads();
	}

	private void disconnectAll() throws Exception {
		for (int i = 0; i < n; i++) {
			synchronized (ds[i]) {
				JLG.debug("disconnecting " + i);
				if (ds[i].isConnected()) {
					ds[i].disconnect();
				}
			}
		}
	}

	private void connectAll() throws Exception {
		ds = new DHTDataSource[n];
		for (int i = 0; i < n; i++) {
			ds[i] = getComponent(DHTDataSource.class).getClass().newInstance();
			ds[i].init();
			ds[i].setName("ds_" + i);
			// unfortunately, the teleal library does not work well with many
			// threads...
			ds[i].tcplistener.removeComponent(NATTraversal.class);
		}
		// first agent
		Properties p = new Properties();
		p.setProperty("agent.isFirst", "yes");
		p.setProperty("listener.tcp.url", "tcp://localhost:" + port);
		ds[0].setConfig(p);

		// other agents
		for (int i = 1; i < n; i++) {
			p = new Properties();
			p.setProperty("agent.isFirst", "no");
			int port_i = port + i;
			p.setProperty("listener.tcp.url", "tcp://localhost:" + port_i);
			p.setProperty("sponsor.1", "tcp://localhost:" + port);
			ds[i].setConfig(p);
		}

		// start all one after the other
		for (int i = 0; i < n; i++) {
			ds[i].connect();
			ContactMap cm = ds[i].getComponent(ContactMap.class);
			JLG.debug("ds[" + i + "] contact map size: " + cm.size());
		}
		for (int i = 0; i < n; i++) {
			ContactMap cm = ds[i].getComponent(ContactMap.class);
			JLG.debug("ds[" + i + "] contact map size: " + cm.size());
		}

	}

}
