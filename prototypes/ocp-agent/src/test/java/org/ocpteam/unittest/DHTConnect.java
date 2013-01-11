package org.ocpteam.unittest;

import static org.junit.Assert.assertEquals;

import java.util.Properties;

import org.junit.Test;
import org.ocpteam.component.ContactMap;
import org.ocpteam.component.NATTraversal;
import org.ocpteam.core.TopContainer;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.LOG;
import org.ocpteamx.protocol.dht0.DHTDataSource;

public class DHTConnect extends TopContainer {

	@Test
	public void dhtconnect() {
		try {
			DHTConnect app = new DHTConnect();
			app.init();
			app.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private int n;
	private int port;
	private DHTDataSource[] ds;
	public boolean stopNow = false;

	public DHTConnect() throws Exception {
		addComponent(DHTDataSource.class);
	}

	@Override
	public void init() throws Exception {
		super.init();
		n = 10;
		port = 40000;
		LOG.debug_on();
		LOG.bUseSet = true;
		LOG.set.add(DHTConnect.class.getName());
		// JLG.set.add(TCPServer.class.getName());
		// JLG.set.add(DHTInterestingStress.class.getName());
		// JLG.set.add(DHTDataModel.class.getName());
		// JLG.set.add(TCPClient.class.getName());
		// JLG.set.add(JLG.class.getName());
		// JLG.set.add(Client.class.getName());
		// JLG.set.add(NATTraversal.class.getName());
	}

	public void start() throws Exception {
		ds = new DHTDataSource[n];
		for (int i = 0; i < n; i++) {
			Class<? extends DHTDataSource> c = getComponent(DHTDataSource.class)
					.getClass();
			ds[i] = c.newInstance();
			ds[i].init();
			ds[i].setName("a" + i);
			// unfortunately, the teleal library does not work well with many
			// threads...
			ds[i].tcplistener.removeComponent(NATTraversal.class);
		}
		// first agent
		Properties p = new Properties();
		p.setProperty("agent.isFirst", "yes");
		p.setProperty("server.port", "" + port);
		ds[0].setConfig(p);

		// other agents
		for (int i = 1; i < n; i++) {
			p = new Properties();
			p.setProperty("agent.isFirst", "no");
			int port_i = port + i;
			p.setProperty("server.port", "" + port_i);
			p.setProperty("sponsor.1", "tcp://localhost:" + port);
			ds[i].setConfig(p);

		}

		// start all
		for (int i = 0; i < n; i++) {
			ds[i].connect();
			ContactMap cm = ds[i].getComponent(ContactMap.class);
			LOG.debug("ds[" + i + "] contact map size: " + cm.size());
			assertEquals(i + 1, cm.size());
		}
		for (int i = 0; i < n; i++) {
			ContactMap cm = ds[i].getComponent(ContactMap.class);
			LOG.debug("ds[" + i + "] contact map size: " + cm.size());
			assertEquals(n, cm.size());
		}

		// disconnect the #1
		LOG.debug("disconnect 1");
		ds[1].disconnect();
		ds[1].getComponent(ContactMap.class).removeAll();
		ds[3].disconnect();
		ds[3].getComponent(ContactMap.class).removeAll();
		for (int i = 0; i < n; i++) {
			ContactMap cm = ds[i].getComponent(ContactMap.class);
			JLG.println("ds[" + i + "] contact map size: " + cm.size());
		}
		ds[0].getComponent(ContactMap.class).refreshContactList();
		ds[2].getComponent(ContactMap.class).refreshContactList();
		ds[5].getComponent(ContactMap.class).refreshContactList();
		for (int i = 0; i < n; i++) {
			ContactMap cm = ds[i].getComponent(ContactMap.class);
			JLG.println("ds[" + i + "] contact map size: " + cm.size());
		}

		LOG.debug("reconnect 1");
		ds[1].connect();
		for (int i = 0; i < n; i++) {
			ContactMap cm = ds[i].getComponent(ContactMap.class);
			JLG.println("ds[" + i + "] contact map size: " + cm.size() + ": " + cm.values());
		}
		LOG.debug("reconnect 3");
		ds[3].connect();
		for (int i = 0; i < n; i++) {
			ContactMap cm = ds[i].getComponent(ContactMap.class);
			JLG.println("ds[" + i + "] contact map size: " + cm.size() + ": " + cm.values());
			assertEquals(n, cm.size());
		}

		for (int i = 0; i < n; i++) {
			ds[i].disconnect();
		}

	}

}
