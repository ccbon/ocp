package org.ocpteam.storage;

import java.io.Serializable;

public abstract class User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected String login;

	public User(String login) {
		this.login = login; 
	}

	public String getLogin() {
		return login;
	}
	
	public abstract String getDefaultLocalDir();



}
