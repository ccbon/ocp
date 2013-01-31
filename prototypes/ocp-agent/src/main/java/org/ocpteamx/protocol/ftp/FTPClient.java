package org.ocpteamx.protocol.ftp;

import java.io.IOException;

import org.ocpteam.component.DSContainer;
import org.ocpteam.entity.Context;
import org.ocpteam.interfaces.IAuthenticable;
import org.ocpteam.interfaces.IConnect;
import org.ocpteam.interfaces.IDataModel;
import org.ocpteam.interfaces.IUserManagement;
import org.ocpteam.misc.LOG;
import org.ocpteam.serializable.User;

public class FTPClient extends DSContainer<FTPDataSource> implements
		IAuthenticable, IConnect {

	private String hostname;
	org.apache.commons.net.ftp.FTPClient ftp;
	private String password;

	public FTPClient() throws Exception {
		ftp = new org.apache.commons.net.ftp.FTPClient();
	}

	@Override
	public void connect() throws Exception {
		hostname = ds().getURI().getHost();
		LOG.info("hostname=" + hostname);
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
	public void authenticate() throws Exception {
		try {
			ftp.connect(hostname);
		} catch (Exception e) {
		}
		IUserManagement a = ds().um;
		String login = a.getUsername();
		String password = (String) getChallenge();
		if (ftp.login(login, password)) {
			LOG.info("ftp logged in.");
			IDataModel dm = new FTPFileSystem(this);
			User user = new User();
			user.setUsername(login);
			ds().setContext(new Context(user, dm, "/"));
		} else {
			throw new Exception("Cannot Login.");
		}
	}

	@Override
	public void setChallenge(Object challenge) {
		this.password = (String) challenge;

	}

	@Override
	public Object getChallenge() {
		return password;
	}
	
	@Override
	public void unauthenticate() throws Exception {
		try {
			ftp.logout();
		} catch (IOException e) {
			// e.printStackTrace();
		}
	}

}
