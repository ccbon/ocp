package org.ocpteam.interfaces;

import java.io.Serializable;

public interface IUser extends Serializable {

	String getUsername();

	void setUsername(String username);

	String getProperty(String key, String defaultValue);

	void setProperty(String key, String value);

}
