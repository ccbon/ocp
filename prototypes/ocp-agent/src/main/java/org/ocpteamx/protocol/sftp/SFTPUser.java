package org.ocpteamx.protocol.sftp;

import org.ocpteam.entity.User;

public class SFTPUser extends User {

	private static final long serialVersionUID = 1L;
	private String dir;

	public SFTPUser(String login, SSHChallenge challenge) {
		super(login);
		dir = challenge.getDefaultLocalDir();
	}

	public String getDefaultLocalDir() {
		return dir;
	}

}
