package org.ocpteam.serializable;

import org.ocpteam.interfaces.IUser;

public class User implements IUser {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected String username;

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public void setUsername(String username) {
		this.username = username;
	}

}
