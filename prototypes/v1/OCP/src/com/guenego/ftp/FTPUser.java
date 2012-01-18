package com.guenego.ftp;

import com.guenego.misc.JLG;
import com.guenego.storage.Agent;
import com.guenego.storage.User;

public class FTPUser extends User {

	private String defaultLocalDir;
	private String password;

	public FTPUser(String login, String password, String defaultLocalDir) {
		super(login);
		this.password = password;
		this.defaultLocalDir = defaultLocalDir;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getDefaultLocalDir() {
		return defaultLocalDir;
	}

	@Override
	public void checkout(Agent agent, String localDir) throws Exception {
		JLG.debug("ftp checkout");
		((FTPAgent) agent).checkout(this, localDir);
		
	}
	
	public String getPassword() {
		return password;
	}

}
