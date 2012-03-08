package org.ocpteam.test;

import java.util.Properties;

import org.ocpteam.layer.rsp.Agent;
import org.ocpteam.misc.JLG;
import org.ocpteam.protocol.ocp.OCPAgent;
import org.ocpteam.protocol.ocp.OCPDataSource;


public class CoupleOfAgent {
	public static void main(String[] args) {
		JLG.debug_on();
		JLG.debug("starting 2 agents");
		JLG.debug("working directory = " + System.getProperty("user.dir"));

		try {

			// start 2 agents
			
			OCPDataSource ds = new OCPDataSource();
			OCPAgent a1 = (OCPAgent) ds.getAgent();
			Properties p1 = ds.getProperties();
			p1.setProperty("name", "Suzana");
			p1.setProperty("server", "yes");
			p1.setProperty("server.listener.1", "tcp://localhost:22220");
			p1.setProperty("server.listener.2", "http://localhost:11110");
			p1.setProperty("server.isFirstAgent", "yes");
			
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
			Agent a2 = ds2.getAgent();
			Properties p2 = ds2.getProperties();
			p2.setProperty("name", "Jean-Louis");
			p2.setProperty("server", "yes");
			p2.setProperty("server.listener.1", "tcp://localhost:22221");
			p2.setProperty("sponsor.1", "tcp://localhost:22220");
			p2.setProperty("sponsor.2", "xxx://localhost:22223");
			a2.connect();
			JLG.debug("done for me.");
		} catch (Exception e) {
			JLG.error(e);
		}
	}

}
