package org.ocpteam.unittest;

import static org.junit.Assert.assertEquals;

import java.util.Properties;
import java.util.Set;

import org.junit.Test;
import org.ocpteam.component.ContactMap;
import org.ocpteam.component.NodeMap;
import org.ocpteam.interfaces.IMapDataModel;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.LOG;
import org.ocpteamx.protocol.dht1.DHT1DataSource;

public class DHT1Test {

	@Test
	public void mytest() {
		//dhtconnect();
		nodeArrival();
	}
	
	
	private void nodeArrival() {
		try {
			int n = 2;
			int port = 40000;
			LOG.debug_on();
			//JLG.bUseSet = true;
			JLG.set.add(DHT1Test.class.getName());
			JLG.set.add(NodeMap.class.getName());

			DHT1DataSource[] ds = new DHT1DataSource[n];
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
			
			ds[0].connect();
			IMapDataModel dm = (IMapDataModel) ds[0].getContext()
					.getDataModel();
			dm.set("hello", "world");

			// start all
			for (int i = 1; i < n; i++) {
				ds[i].connect();
				ContactMap cm = ds[i].getComponent(ContactMap.class);
				LOG.debug("ds[" + i + "] contact map size: " + cm.size());
				assertEquals(i + 1, cm.size());
			}
			
			
			for (int i = 0; i < n - 1; i++) {
				ds[i].disconnect();
			}
			
			dm = (IMapDataModel) ds[n-1].getContext().getDataModel();
			String value = dm.get("hello");
			LOG.debug("hello->" + value);
			assertEquals("world", value);
			ds[n-1].disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}


	public void dhtconnect() {
		try {
			int n;
			int port;
			DHT1DataSource[] ds;

			n = 40;
			port = 40000;
			LOG.debug_on();
			JLG.bUseSet = true;
			JLG.set.add(DHT1Test.class.getName());
			JLG.set.add(NodeMap.class.getName());

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
				LOG.debug("ds[" + i + "] contact map size: " + cm.size());
				assertEquals(i + 1, cm.size());
			}
			for (int i = 0; i < n; i++) {
				NodeMap nm = ds[i].getComponent(NodeMap.class);
				LOG.debug("nodeMap size:" + nm.getNodeMap().size());
				LOG.debug("nodeMap:" + nm.getNodeMap());
			}

			IMapDataModel dm = (IMapDataModel) ds[4].getContext()
					.getDataModel();
			dm.set("hello", "world");
			String value = dm.get("hello");
			LOG.debug("hello->" + value);
			LOG.debug("hash(hello) = " + ds[4].hash("hello".getBytes()));

			dm.set("coucou", "Suzana");
			for (int i = 0; i < 100; i++) {
				dm.set("coucou" + i, "Suzana" + i);
			}

			Set<String> keyset = dm.keySet();
			LOG.debug("keyset: " + keyset);
			LOG.debug("keyset size: " + keyset.size());
			LOG.debug_off();
			for (int i = 0; i < n; i++) {
				ds[i].disconnect();
			}
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals(0, 1);
		}
	}

}
