package org.ocpteam.test;

import org.apache.commons.net.ProtocolCommandEvent;
import org.apache.commons.net.ProtocolCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.ocpteam.misc.JLG;


public class TestFTP {
	public static void main(String[] args) {
		JLG.debug_on();
		FTPClient f = new FTPClient();
		try {
			f.connect("ftp.guenego.com");

			f.addProtocolCommandListener(new ProtocolCommandListener() {

				@Override
				public void protocolReplyReceived(ProtocolCommandEvent arg0) {
					JLG.debug("RECEIVED:");
					// JLG.debug(arg0.toString());
					JLG.debug(arg0.getCommand());
					JLG.debug(arg0.getMessage());
				}

				@Override
				public void protocolCommandSent(ProtocolCommandEvent arg0) {
					JLG.debug("SENT:");
					JLG.debug(arg0.toString());
					JLG.debug(arg0.getCommand());
					JLG.debug(arg0.getMessage());

				}
			});
			f.login("guenego", "maou1805");
			f.features();
			String[] names = f.listNames();
			for (String name : names) {
				System.out.println("Name = " + name);
			}
			f.logout();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
