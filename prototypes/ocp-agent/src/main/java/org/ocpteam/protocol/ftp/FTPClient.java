package org.ocpteam.protocol.ftp;

import java.io.IOException;

import org.ocpteam.component.Authentication;
import org.ocpteam.component.DSContainer;
import org.ocpteam.entity.Context;
import org.ocpteam.entity.User;
import org.ocpteam.interfaces.IAuthenticable;
import org.ocpteam.interfaces.IConnect;
import org.ocpteam.interfaces.IDataModel;
import org.ocpteam.misc.JLG;

public class FTPClient extends DSContainer<FTPDataSource> implements IAuthenticable, IConnect {

	private String hostname;
	org.apache.commons.net.ftp.FTPClient ftp;

	public FTPClient() throws Exception {
		ftp = new org.apache.commons.net.ftp.FTPClient();
	}

	@Override
	public void connect() throws Exception {
		hostname = ds().getURI().getHost();
		JLG.debug("hostname=" + hostname);
		try {
			ftp.connect(hostname);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Cannot connect to " + hostname);
		}
	}

	@Override
	public void disconnect() throws Exception {
		try {
			ftp.logout();
		} catch (IOException e) {
			// e.printStackTrace();
		}
	}

	@Override
	public void login() throws Exception {
		logout();
		try {
			ftp.connect(hostname);
		} catch (Exception e) {
		}
		Authentication a = ds().authentication;
		String login = a.getUsername();
		String password = (String) a.getChallenge();
		if (ftp.login(login, password)) {
			JLG.debug("ftp logged in.");
			IDataModel dm = new FTPFileSystem(this);
			ds().setContext(new Context(dm, "/"));
			User user = new User(login);
			a.setUser(user);
		} else {
			throw new Exception("Cannot Login.");
		}
	}

	@Override
	public void logout() throws Exception {
		try {
			ftp.logout();
		} catch (Exception e) {
		}
	}

}
