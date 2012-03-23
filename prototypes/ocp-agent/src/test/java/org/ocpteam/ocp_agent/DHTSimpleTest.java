package org.ocpteam.ocp_agent;

import static org.junit.Assert.assertTrue;

import java.util.Properties;

import org.ocpteam.component.DataSource;
import org.ocpteam.misc.JLG;
import org.ocpteam.protocol.dht.DHTDataSource;

public class DHTSimpleTest {

	@org.junit.Test
	public void simple() {
		try {
			assertTrue(new DHTSimpleTest().test());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean test() {
		try {
			JLG.debug_on();
			System.out.println("Hello Test Scenario");
			DataSource ds = new DHTDataSource();
			ds.init();
			Properties p = new Properties();
			p.setProperty("network.coucou", "23");
			p.setProperty("network.hello", "456a");
			p.setProperty("server", "yes");
			p.setProperty("listener.tcp.url", "tcp://localhost:12345");
			ds.setConfig(p);
			ds.connect();
			ds.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
