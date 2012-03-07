package org.ocpteam.test;

import java.util.Iterator;
import java.util.Properties;

import org.ocpteam.layer.rsp.Authentication;
import org.ocpteam.layer.rsp.DataSource;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.JLGException;
import org.ocpteam.protocol.ocp.Captcha;
import org.ocpteam.protocol.ocp.OCPAgent;
import org.ocpteam.protocol.ocp.OCPDataSource;
import org.ocpteam.protocol.ocp.OCPFileSystem;
import org.ocpteam.protocol.ocp.OCPUser;
import org.ocpteam.protocol.ocp.Pointer;


public class TestAgent {
	public static void main(String[] args) {
		JLG.debug_on();
		JLG.debug("starting the test agent");
		JLG.debug("working directory = " + System.getProperty("user.dir"));

		try {
			JLG.rm(System.getenv("TEMP") + "/ocp_agent_storage");
			// start 2 agents
			DataSource ds1 = new OCPDataSource();
			OCPAgent a1 = (OCPAgent) ds1.getAgent();
			Properties p1 = ds1.getProperties();
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
			JLG.debug(a1.toString());
			

			// starting second agent
			DataSource ds2 = new OCPDataSource();
			OCPAgent a2 = (OCPAgent) ds2.getAgent();
			Properties p2 = ds2.getProperties();
			p2.setProperty("name", "Jean-Louis");
			p2.setProperty("server", "yes");
			p2.setProperty("server.listener.1", "tcp://localhost:22221");
			p2.setProperty("sponsor.1", "tcp://localhost:22220");
			p2.setProperty("sponsor.2", "xxx://localhost:22223");
			a2.connect();
			Thread.sleep(2000);
			JLG.debug(a1.toString());
			JLG.debug(a2.toString());
			//System.exit(0);
			
			String username = "jlguenego";
			String password = "jlouis";
			Captcha captcha = a2.wantToCreateUser(username, password);
			
			//String answer = JLG.input("captcha challenge: " + captcha.challengeObject + "> ");
			String answer = "didounette";
			a2.createUser(username, password, 2, captcha, answer);
			JLG.debug(a1.toString());
			JLG.debug(a2.toString());
			//System.exit(0);
			
			// test ucrypt and udecrypt
			String message = "this is my message";
			JLG.debug(message);
			byte[] ciphertext = a2.ucrypt(password, message.getBytes());
			String decryptedMessage = new String(a2.udecrypt(password, ciphertext));
			JLG.debug(decryptedMessage);
			//System.exit(0);
			
			Authentication auth = new Authentication(ds2, username, password);
			auth.login();
			OCPUser user = (OCPUser) auth.getUser();
			JLG.debug(user.toString());
			
			
			String str = "This is my object";
			Pointer pointer = a2.set(user, str);
			JLG.debug(a1.toString());
			JLG.debug(a2.toString());
			//System.exit(0);
			
			
			str = "This is my second object";
			a2.set(user, str);

			String str2 = (String) a2.get(user, pointer);
			JLG.debug("str2 = " + str2);
			Iterator<Pointer> it = user.getUserIndex(a2).iterator();
			while (it.hasNext()) {
				Pointer p = (Pointer) it.next();
				JLG.debug("pointer p = " + p);
				String str3 = (String) a2.get(user, p);
				JLG.debug("str3 = " + str3);
			}
			a2.remove(user, pointer);
			it = user.getUserIndex(a2).iterator();
			while (it.hasNext()) {
				Pointer p = (Pointer) it.next();
				JLG.debug("pointer p = " + p);
				String str3 = (String) a2.get(user, p);
				JLG.debug("str3 = " + str3);
				a2.remove(user, p);
			}

			JLG.debug(a1.toString());
			JLG.debug(a2.toString());

			OCPFileSystem fs = new OCPFileSystem(user, a2);
			fs.checkoutAll("C:/jlouis/ocp_dir");
			
			JLG.setFile("C:/jlouis/ocp_dir/first.txt", "this is my first file.");
			JLG.setFile("C:/jlouis/ocp_dir/second.txt", "this is my second file.");
			fs.commitAll("C:/jlouis/ocp_dir");
			
			OCPFileSystem fs2 = new OCPFileSystem(user, a2);
			fs2.checkoutAll("C:/jlouis/ocp_dir2");
			
			a1.disconnect();
			a2.disconnect();

		} catch (JLGException e) {
			System.exit(1);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		JLG.debug("finished.");
	}

}
