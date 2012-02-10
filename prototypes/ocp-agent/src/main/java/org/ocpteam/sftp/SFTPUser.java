package org.ocpteam.sftp;

import org.ocpteam.storage.User;

public class SFTPUser extends User {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
