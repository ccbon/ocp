package org.ocpteam.ocp_agent;

import static org.junit.Assert.assertTrue;

import java.util.Properties;

import org.ocpteam.component.Agent;
import org.ocpteam.component.DataSource;
import org.ocpteam.misc.JLG;
import org.ocpteam.protocol.ocp.OCPAgent;
import org.ocpteam.protocol.ocp.OCPDataSource;

public class OCPSimpleTest {

	@org.junit.Test
	public void test() {
		try {
			//assertTrue(new OCPSimpleTest().simple());
			assertTrue(new OCPSimpleTest().twin());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean twin() {
		JLG.debug_on();
		JLG.debug("starting 2 agents");
		JLG.debug("working directory = " + System.getProperty("user.dir"));

		try {

			// start 2 agents
			
			OCPDataSource ds = new OCPDataSource();
			ds.init();
			OCPAgent a1 = (OCPAgent) ds.getComponent(Agent.class);
			Properties p1 = ds.getConfig();
			p1.setProperty("name", "Suzana");
			p1.setProperty("server", "yes");
			p1.setProperty("server.listener.1", "tcp://localhost:22220");
			p1.setProperty("server.listener.2", "http://localhost:11110");
			p1.setProperty("agent.isFirst", "yes");
			
			Properties network = new Properties();
			network.setProperty("hello", "didounette");
			network.setProperty("coucou", "jlg");
			network.setProperty("hash", "SHA-1");
			network.setProperty("backupNbr", "2");
			a1.setNetworkProperties(network);
			a1.connect();

			// UserInterface ui = new CommandLine(agent);
			// (new Thread(ui)).start();
			Thread.sleep(2000);

			// starting second agent
			OCPDataSource ds2 = new OCPDataSource();
			ds2.init();
			OCPAgent a2 = (OCPAgent) ds2.getComponent(Agent.class);
			Properties p2 = ds2.getConfig();
			p2.setProperty("name", "Jean-Louis");
			p2.setProperty("server", "yes");
			p2.setProperty("server.listener.1", "tcp://localhost:22221");
			p2.setProperty("agent.isFirst", "no");
			p2.setProperty("sponsor.1", "tcp://localhost:22220");
			p2.setProperty("sponsor.2", "xxx://localhost:22223");
			a2.connect();
			JLG.debug("done for me.");
			JLG.debug("contact map size:" + ds2.contactMap.getContactSnapshotList().size());
			JLG.debug("contact map: " + ds2.contactMap.getContactSnapshotList().toString());
		} catch (Exception e) {
			JLG.error(e);
			return false;
		}
		return true;
	}

	public boolean simple() {
		try {
			System.out.println("Hello Test Scenario");
			DataSource ds = new OCPDataSource();
			ds.connect();
			ds.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
