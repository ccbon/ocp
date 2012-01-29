package org.ocpteam.sftp;

import org.ocpteam.storage.User;

public class SFTPUser extends User {

	private SSHChallenge challenge;
	private String dir;

	public SFTPUser(String login, SSHChallenge challenge) {
		super(login);
		this.challenge = challenge;
		dir = challenge.getDefaultLocalDir();
	}

	@Override
	public String getDefaultLocalDir() {
		return dir;
	}

}
