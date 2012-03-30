package org.ocpteam.ocp_agent;

import static org.junit.Assert.assertEquals;

import java.util.Properties;

import org.junit.Test;
import org.ocpteam.component.ContactMap;
import org.ocpteam.component.NATTraversal;
import org.ocpteam.core.TopContainer;
import org.ocpteam.misc.JLG;
import org.ocpteam.protocol.dht.DHTDataSource;

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
		JLG.debug_on();
		JLG.bUseSet = true;
		//JLG.set.add(TCPServer.class.getName());
		//JLG.set.add(DHTInterestingStress.class.getName());
		//JLG.set.add(DHTDataModel.class.getName());
		//JLG.set.add(TCPClient.class.getName());
		//JLG.set.add(JLG.class.getName());
		//JLG.set.add(Client.class.getName());
		//JLG.set.add(NATTraversal.class.getName());
	}

	public void start() throws Exception {
		ds = new DHTDataSource[n];
		for (int i = 0; i < n; i++) {
			Class<? extends DHTDataSource> c = getComponent(DHTDataSource.class)
					.getClass();
			ds[i] = c.newInstance();
			ds[i].init();
			ds[i].setName("agent_" + i); 
			// unfortunately, the teleal library does not work well with many
			// threads...
			ds[i].listener.removeComponent(NATTraversal.class);
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

		// start all
		for (int i = 0; i < n; i++) {
			ds[i].connect();
			ContactMap cm = ds[i].getComponent(ContactMap.class);
			JLG.println("ds[" + i + "] contact map size: " + cm.size());
			assertEquals(i + 1, cm.size());
		}
		for (int i = 0; i < n; i++) {
			ContactMap cm = ds[i].getComponent(ContactMap.class);
			JLG.println("ds[" + i + "] contact map size: " + cm.size());
			assertEquals(10, cm.size());
		}
		
		for (int i = 0; i < n; i++) {
			ds[i].disconnect();
		}
		
	}

}
