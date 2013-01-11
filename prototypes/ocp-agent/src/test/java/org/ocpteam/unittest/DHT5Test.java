package org.ocpteam.unittest;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Properties;

import org.junit.Test;
import org.ocpteam.component.ContactMap;
import org.ocpteam.component.DataSource;
import org.ocpteam.entity.Context;
import org.ocpteam.interfaces.IAuthenticable;
import org.ocpteam.interfaces.IFileSystem;
import org.ocpteam.interfaces.IPersistentMap;
import org.ocpteam.interfaces.IUserCreation;
import org.ocpteam.interfaces.IUserManagement;
import org.ocpteam.misc.Id;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.LOG;
import org.ocpteam.misc.TestUtils;
import org.ocpteamx.protocol.dht5.DHT5v2DataSource;

public class DHT5Test {

	@Test
	public void mytest() {
		test();
	}

	private void test() {
		try {
			String filename = "test.txt";
			int n = 2;
			int port = 40000;
			LOG.debug_on();
			JLG.bUseSet = true;
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
			ds[0].connect();

			
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
			dts.getComponent(IAuthenticable.class).setChallenge(password);
			um.login();
			
			TestUtils.createBigFile(filename);
			Id checksum = TestUtils.checksum(filename);
			
			Context ctx = dts.getContext();
			IFileSystem fs = (IFileSystem) ctx.getDataModel();
			LOG.debug("commit " + filename);
			fs.commit("/", new File(filename));
			JLG.rm(filename);
			
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
			for (int i = 1; i < n; i++) {
				ds[i].connect();
				ContactMap cm = ds[i].getComponent(ContactMap.class);
				LOG.debug("ds[" + i + "] contact map size: " + cm.size());
				assertEquals(i + 1, cm.size());
			}
			
			dts = ds[1];
			um = dts.getComponent(IUserManagement.class);
			um.setUsername(username);
			dts.getComponent(IAuthenticable.class).setChallenge(password);
			um.login();

			ctx = dts.getContext();
			fs = (IFileSystem) ctx.getDataModel();
			LOG.debug("checkout" + filename);
			fs.checkout("/", filename, new File("."));
			Id checksum2 = TestUtils.checksum(filename);
			
			for (int i = 0; i < n; i++) {
				LOG.debug("disconnecting " + ds[i].getName());
				ds[i].disconnectHard();
			}
			
			JLG.rm(filename);
			LOG.debug("checksum=" + checksum);
			LOG.debug("checksum2=" + checksum2);
			assertEquals(checksum, checksum2);
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals(0, 1);
		}

	}

}
