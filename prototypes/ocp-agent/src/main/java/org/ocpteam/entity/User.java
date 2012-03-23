package org.ocpteam.entity;

import java.io.Serializable;

public class User implements Serializable {

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
	
	public String getDefaultLocalDir() {
		return System.getProperty("user.home");
	}



}