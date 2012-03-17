package org.ocpteam.protocol.ftp;

import java.io.IOException;

import org.ocpteam.component.Authentication;
import org.ocpteam.component.Client;
import org.ocpteam.component.IDataModel;
import org.ocpteam.layer.rsp.IAuthenticable;
import org.ocpteam.layer.rsp.Context;
import org.ocpteam.layer.rsp.User;
import org.ocpteam.misc.JLG;

public class FTPClient extends Client implements IAuthenticable {

	private String hostname;
	org.apache.commons.net.ftp.FTPClient ftp;

	public FTPClient() {
		ftp = new org.apache.commons.net.ftp.FTPClient();
	}

	@Override
	public void connect() throws Exception {
		hostname = ds.getURI().getHost();
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
		Authentication a = ds.getDesigner().get(Authentication.class);
		String login = a.getLogin();
		String password = (String) a.getChallenge();
		if (ftp.login(login, password)) {
			JLG.debug("ftp logged in.");
			IDataModel dm = new FTPFileSystem(this);
			ds.setContext(new Context(dm, "/"));
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
