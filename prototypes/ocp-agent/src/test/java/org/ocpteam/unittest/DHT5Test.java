package org.ocpteam.unittest;

import static org.junit.Assert.assertEquals;

import java.util.Properties;

import org.junit.Test;
import org.ocpteam.component.ContactMap;
import org.ocpteam.component.DataSource;
import org.ocpteam.interfaces.IPersistentMap;
import org.ocpteam.interfaces.IUserCreation;
import org.ocpteam.interfaces.IUserManagement;
import org.ocpteam.misc.JLG;
import org.ocpteamx.protocol.dht5.DHT5v2DataSource;

public class DHT5Test {

	@Test
	public void mytest() {
		test();
	}

	private void test() {
		try {
			int n = 10;
			int port = 40000;
			JLG.debug_on();
			//JLG.bUseSet = true;
			JLG.set.add(DHT5Test.class.getName());
			DHT5v2DataSource[] ds = new DHT5v2DataSource[n];
			for (int i = 0; i < n; i++) {
				ds[i] = new DHT5v2DataSource();
				ds[i].init();
				ds[i].setName("a" + i);
				ds[i].readConfig();
				ds[i].getComponent(IPersistentMap.class).clear();
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
			
			// create a user
			DataSource dts = ds[0];
			String username = "jlouis";
			String password = "#####";
			IUserCreation uc = dts.getComponent(IUserCreation.class);
			uc.setUser(username);
			uc.setPassword(password);
			uc.createUser();
			IUserManagement um = dts.getComponent(IUserManagement.class);
			um.setUsername(username);
			um.setChallenge(password);
			um.login();
			
			for (int i = 0; i < n; i++) {
				JLG.debug("disconnecting " + ds[i].getName());
				ds[i].disconnectHard();
			}

		} catch (Exception e) {
			e.printStackTrace();
			assertEquals(0, 1);
		}

	}

}
