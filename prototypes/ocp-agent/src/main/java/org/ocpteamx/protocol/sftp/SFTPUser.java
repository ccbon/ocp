package org.ocpteamx.protocol.sftp;

import org.ocpteam.serializable.User;

public class SFTPUser extends User {

	private static final long serialVersionUID = 1L;
	private String dir;

	public SFTPUser(String login, SSHChallenge challenge) {
		super();
		setUsername(login);
		dir = challenge.getDefaultLocalDir();
	}

	public String getDefaultLocalDir() {
		return dir;
	}

}
