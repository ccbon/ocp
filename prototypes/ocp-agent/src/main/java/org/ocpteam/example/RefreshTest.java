package org.ocpteam.example;

import java.util.Properties;

import org.ocpteam.component.DSPDataSource;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.LOG;
import org.ocpteamx.protocol.dht0.DHTDataSource;

public class RefreshTest {
	public static void main(String[] args) {
		try {
			LOG.debug_on();
			JLG.bUseSet = true;
			JLG.set.add(RefreshTest.class.getName());
			// JLG.set.add(Client.class.getName());

			int n = 10;
			int port = 40000;
			DSPDataSource[] ds = new DSPDataSource[n];
			for (int i = 0; i < n; i++) {
				ds[i] = new DHTDataSource();
				ds[i].init();
				ds[i].setName("ds_" + i);
			}
			Properties p = new Properties();
			p.setProperty("agent.isFirst", "yes");
			p.setProperty("server.port", "" + port);
			ds[0].setConfig(p);
			ds[0].connect();
			LOG.debug("0: contactMap size: " + ds[0].contactMap.size());
			ds[0].contactMap.refreshContactList();
			ds[0].client.waitForCompletion();

			for (int i = 1; i < n; i++) {
				p = new Properties();
				int port_i = port + i;
				p.setProperty("agent.isFirst", "no");
				p.setProperty("server.port", "" + port_i);
				p.setProperty("sponsor.1", "tcp://localhost:" + port);
				ds[i].setConfig(p);
				ds[i].connect();
			}
			for (int i = 0; i < n; i++) {
				LOG.debug(i + ": contactMap size: " + ds[i].contactMap.size());
				ds[i].contactMap.refreshContactList();
				ds[i].client.waitForCompletion();

			}
			for (int i = 0; i < n - 1; i++) {
				ds[i].disconnect();
			}
			ds[n-1].contactMap.refreshContactList();
			ds[n-1].client.waitForCompletion();
			LOG.debug((n-1) + ": contactMap size: " + ds[n-1].contactMap.size());

			ds[n-1].disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
