package org.ocpteam.unittest;

import static org.junit.Assert.assertEquals;

import java.util.Properties;

import org.junit.Test;
import org.ocpteam.component.ContactMap;
import org.ocpteam.misc.JLG;
import org.ocpteam.protocol.dht2.DHT2DataSource;

public class DHT2Test {

	@Test
	public void mytest() {
		test();
	}

	private void test() {
		try {
			int n = 10;
			int port = 40000;
			JLG.debug_on();
			JLG.bUseSet = true;
			JLG.set.add(DHT2Test.class.getName());
			//JLG.set.add(DHT2DataSource.class.getName());
			//JLG.set.add(RingNodeMap.class.getName());

			DHT2DataSource[] ds = new DHT2DataSource[n];
			for (int i = 0; i < n; i++) {
				ds[i] = new DHT2DataSource();
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
			ds[0].dm.set("hello", "world");

			// start all
			for (int i = 1; i < n; i++) {
				ds[i].connect();
				ContactMap cm = ds[i].getComponent(ContactMap.class);
				JLG.debug("ds[" + i + "] contact map size: " + cm.size());
				assertEquals(i + 1, cm.size());
			}
			ds[0].dm.set("coucou", "suzana");
			ds[0].dm.remove("hello");
			ds[0].networkPicture();
			
			ds[0].disconnectHard();
			ds[1].contactMap.refreshContactList();
			ds[1].client.waitForCompletion();
			JLG.debug("-------------------------------------");
			ds[1].networkPicture();
			
			JLG.debug("coucou->" + ds[1].dm.get("coucou"));
			for (int i = 1; i < n; i++) {
				JLG.debug("disconnecting " + ds[i].getName());
				ds[i].disconnectHard();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
