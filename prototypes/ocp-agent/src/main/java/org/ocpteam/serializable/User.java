package org.ocpteam.serializable;

import java.util.Properties;

import org.ocpteam.interfaces.IStructurable;
import org.ocpteam.interfaces.IUser;
import org.ocpteam.misc.Structure;

public class User implements IUser, IStructurable {

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

	@Override
	public Structure toStructure() throws Exception {
		Structure result = new Structure(getClass());
		result.setStringField("username", username);
		result.setProprietiesField("props", properties);
		return result;
	}

	@Override
	public void fromStructure(Structure s) throws Exception {
		username = s.getString("username");
		properties = s.getProperties("props");
	}
}
