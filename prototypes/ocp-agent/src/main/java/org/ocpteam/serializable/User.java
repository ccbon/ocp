package org.ocpteam.serializable;

import java.util.Properties;

import org.ocpteam.interfaces.IUser;

public class User implements IUser {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected String username;
	protected Properties properties = new Properties();

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public void setUsername(String username) {
		this.username = username;
	}
	
	@Override
	public String getProperty(String key, String defaultValue) {
		return properties.getProperty(key, defaultValue);
	}

	@Override
	public void setProperty(String key, String value) {
		properties.setProperty(key, value);		
	}

}
