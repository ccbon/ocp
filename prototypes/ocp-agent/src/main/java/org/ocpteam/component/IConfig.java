package org.ocpteam.component;

import java.util.Iterator;
import java.util.Properties;

public interface IConfig {
	Properties getConfig();
	void setConfig(Properties p);
	String get(String key);
	String get(String key, String defaultValue);
	void set(String key, String value);
	Iterator<String> iterator();
}
