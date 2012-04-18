package org.ocpteam.unittest;

import static org.junit.Assert.assertEquals;

import java.util.Properties;

import org.junit.Test;
import org.ocpteam.component.ContactMap;
import org.ocpteam.component.NodeMap;
import org.ocpteam.core.TopContainer;
import org.ocpteam.interfaces.IDataModel;
import org.ocpteam.interfaces.IMapDataModel;
import org.ocpteam.misc.JLG;
import org.ocpteam.protocol.dht1.DHT1DataSource;

public class DHT1Test extends TopContainer {

	@Test
	public void dhtconnect() {
		try {
			DHT1Test app = new DHT1Test();
			app.init();
			app.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private int n;
	private int port;
	private DHT1DataSource[] ds;
	public boolean stopNow = false;

	public DHT1Test() throws Exception {
	}

	@Override
	public void init() throws Exception {
		super.init();
		n = 40;
		port = 40000;
		JLG.debug_on();
		JLG.bUseSet = true;
		JLG.set.add(DHT1Test.class.getName());
		JLG.set.add(NodeMap.class.getName());
	}

	public void start() throws Exception {
		ds = new DHT1DataSource[n];
		for (int i = 0; i < n; i++) {
			ds[i] = new DHT1DataSource();
			ds[i].init();
			ds[i].setName("a" + i);
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
			JLG.debug("ds[" + i + "] contact map size: " + cm.size());
			assertEquals(i + 1, cm.size());
		}
		for (int i = 0; i < n; i++) {
			NodeMap nm = ds[i].getComponent(NodeMap.class);
			JLG.debug("nodeMap size:" + nm.getNodeMap().size());
			JLG.debug("nodeMap:" + nm.getNodeMap());
		}
		
		IMapDataModel dm = (IMapDataModel) ds[4].getContext().getDataModel();
		dm.set("hello", "world");
		String value = dm.get("hello");
		JLG.debug("hello->" + value);
		JLG.debug("hash(hello) = " + ds[4].hash("hello".getBytes()));
		
		JLG.debug_off();
		for (int i = 0; i < n; i++) {
			ds[i].disconnect();
		}

	}

}
