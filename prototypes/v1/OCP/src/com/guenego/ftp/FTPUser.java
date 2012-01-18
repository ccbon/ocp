package com.guenego.ftp;

import com.guenego.misc.JLG;
import com.guenego.storage.Agent;
import com.guenego.storage.User;

public class FTPUser extends User {

	private String defaultLocalDir;

	public FTPUser(String login, String defaultLocalDir) {
		super(login);
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
	public void checkout(Agent agent, String text) throws Exception {
		JLG.debug("ftp checkout");
		// TODO Auto-generated method stub
		
	}

}
