package org.ocpteam.protocol.sftp;

import org.ocpteam.layer.rsp.User;

public class SFTPUser extends User {

	private static final long serialVersionUID = 1L;
	private String dir;

	public SFTPUser(String login, SSHChallenge challenge) {
		super(login);
		dir = challenge.getDefaultLocalDir();
	}

	@Override
	public String getDefaultLocalDir() {
		return dir;
	}

}
