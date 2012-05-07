package org.ocpteam.entity;

import org.ocpteam.interfaces.IUser;

public class User implements IUser {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected String username;

	public User(String username) {
		this.username = username;
	}

	@Override
	public String getUsername() {
		return username;
	}

}
