package org.ocpteam.unittest;

import static org.junit.Assert.assertTrue;

import java.util.Properties;

import org.ocpteam.misc.Application;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.LOG;
import org.ocpteamx.protocol.ocp.Captcha;
import org.ocpteamx.protocol.ocp.OCPAgent;
import org.ocpteamx.protocol.ocp.OCPDataSource;

public class OCPSimpleTest {

	@org.junit.Test
	public void test() {
		try {
			Application.setAppDir(Application.TESTDIR);
			//assertTrue(new OCPSimpleTest().simple());
			assertTrue(new OCPSimpleTest().twin());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean twin() {
		LOG.debug_on();	
		LOG.info("starting 2 agents");
		LOG.info("working directory = " + System.getProperty("user.dir"));
		JLG.rm(Application.TESTDIR + "/datastore/ocp/");
		try {

			// start 2 agents
			
			OCPDataSource ds = new OCPDataSource();
			ds.init();
			Properties p1 = ds.getConfig();
			p1.setProperty("name", "Suzana");
			p1.setProperty("server", "yes");
			p1.setProperty("server.listener.1", "tcp://localhost:22220");
			p1.setProperty("server.listener.2", "http://localhost:11110");
			p1.setProperty("agent.isFirst", "yes");
			p1.setProperty("network.hello", "didounette");
			p1.setProperty("network.coucou", "jlg");
			p1.setProperty("network.hash", "SHA-1");
			p1.setProperty("network.backupNbr", "2");
			ds.connect();

			
			// UserInterface ui = new CommandLine(agent);
			// (new Thread(ui)).start();
			Thread.sleep(2000);

			// starting second agent
			OCPDataSource ds2 = new OCPDataSource();
			ds2.init();
			Properties p2 = ds2.getConfig();
			p2.setProperty("name", "Jean-Louis");
			p2.setProperty("server", "yes");
			p2.setProperty("server.listener.1", "tcp://localhost:22221");
			p2.setProperty("agent.isFirst", "no");
			p2.setProperty("sponsor.1", "tcp://localhost:22220");
			p2.setProperty("sponsor.2", "xxx://localhost:22223");
			ds2.connect();
			LOG.info("done for me.");
			LOG.info("contact map size:" + ds2.contactMap.getContactSnapshotList().size());
			LOG.info("contact map: " + ds2.contactMap.getContactSnapshotList().toString());
			LOG.info("contact map size:" + ds.contactMap.getContactSnapshotList().size());
			LOG.info("contact map: " + ds.contactMap.getContactSnapshotList().toString());

			OCPAgent a2 = ds2.getComponent(OCPAgent.class);
			String username = "jlguenego";
			String password = "jlouis";
			Captcha captcha = a2.wantToCreateUser(username, password);
			LOG.info("captcha received: " + captcha);
			//String answer = JLG.input("captcha challenge: " + captcha.challengeObject + "> ");
			String answer = "didounette";
			a2.createUser(username, password, 2, captcha, answer);
			LOG.info(a2.toString());
			
			
			ds2.disconnect();
			ds.disconnect();
		} catch (Exception e) {
			LOG.error(e);
			return false;
		}
		return true;
	}

	public boolean simple() {
		try {
			System.out.println("Hello Test Scenario");
			OCPDataSource ds = new OCPDataSource();
			ds.connect();
			ds.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
