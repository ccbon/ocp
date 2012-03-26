package org.ocpteam.interfaces;

import java.util.Iterator;
import java.util.Properties;

public interface IConfig {
	Properties getConfig();
	void setConfig(Properties p);
	String getProperty(String key);
	String getProperty(String key, String defaultValue);
	void setProperty(String key, String value);
	Iterator<String> iterator();
}
