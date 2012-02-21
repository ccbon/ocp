package org.ocpteam.layer.rsp;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public abstract class Agent {

	public Properties p;

	protected boolean bIsStarted = false;

	public Agent() {
	}

	public abstract boolean isConfigFilePresent();

	public void loadConfig() throws Exception {
		if (requiresConfigFile() && !isConfigFilePresent()) {
			throw new Exception("Config file is not found. Expected Path: "
					+ getConfigFile().getAbsolutePath());
		}
		p = new Properties();
		if (isConfigFilePresent()) {
			p.load(new FileInputStream(getConfigFile()));
		}
		readConfig();
	}

	public abstract File getConfigFile();

	public void loadConfig(Properties properties) throws Exception {
		p = properties;
		readConfig();
	}

	protected abstract void readConfig() throws Exception;

	public abstract void start() throws Exception;

	public abstract void stop();

	public abstract boolean allowsUserCreation();

	public abstract User login(String login, Object challenge) throws Exception;
	
	public abstract void logout(User user) throws Exception;
	
	public abstract String getProtocolName();

	public abstract String getName();

	public String getHelpURL() {
		return "http://code.google.com/p/ocp/wiki/Help";
	}


	public abstract FileSystem getFileSystem(User user);

	public abstract boolean autoStarts();

	public boolean isStarted() {
		return bIsStarted;
	}

	public boolean connectsWithSSH() {
		return false;
	}

	public boolean requiresConfigFile() {
		return true;
	}

	public abstract boolean isOnlyClient();

}
