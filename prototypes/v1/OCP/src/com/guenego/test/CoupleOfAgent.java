package com.guenego.test;

import java.util.Iterator;
import java.util.Properties;

import com.guenego.misc.JLG;
import com.guenego.misc.JLGException;
import com.guenego.ocp.Agent;
import com.guenego.ocp.Captcha;
import com.guenego.ocp.FileSystem;
import com.guenego.ocp.Pointer;
import com.guenego.ocp.User;

public class CoupleOfAgent {
	public static void main(String[] args) {
		JLG.debug_on();
		JLG.debug("starting 2 agents");
		JLG.debug("working directory = " + System.getProperty("user.dir"));

		try {

			// start 2 agents
			Properties p1 = new Properties();
			p1.setProperty("name", "Suzana");
			p1.setProperty("server", "yes");
			p1.setProperty("server.listener.1", "tcp://localhost:22220");
			p1.setProperty("server.listener.2", "http://localhost:11110");
			p1.setProperty("server.isFirstAgent", "yes");
			Agent a1 = new Agent(p1);
			Properties network = new Properties();
			network.setProperty("hello", "didounette");
			network.setProperty("coucou", "jlg");
			network.setProperty("hash", "SHA-1");
			network.setProperty("backupNbr", "2");
			a1.setNetworkProperties(network);
			a1.start();

			// UserInterface ui = new CommandLine(agent);
			// (new Thread(ui)).start();
			Thread.sleep(2000);

			// starting second agent
			Properties p2 = new Properties();
			p2.setProperty("name", "Jean-Louis");
			p2.setProperty("server", "yes");
			p2.setProperty("server.listener.1", "tcp://localhost:22221");
			p2.setProperty("sponsor.1", "tcp://localhost:22220");
			p2.setProperty("sponsor.2", "xxx://localhost:22223");
			Agent a2 = new Agent(p2);
			a2.start();
			JLG.debug("done for me.");
		} catch (Exception e) {
			JLG.error(e);
		}
	}

}
