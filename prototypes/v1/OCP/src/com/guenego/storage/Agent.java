package com.guenego.storage;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.Properties;

import com.guenego.misc.JLG;
import com.guenego.ocp.Contact;

public abstract class Agent {

	public Properties p;

	public Agent() {
	}

	public void loadConfig() throws Exception {
		if (!isConfigFilePresent()) {
			throw new Exception("Config file is not found. Expected Path: "
					+ getConfigFile().getAbsolutePath());
		}
		p = new Properties();
		p.load(new FileInputStream(getConfigFile()));
		readConfig();
	}

	public abstract File getConfigFile();

	public void loadConfig(Properties properties) throws Exception {
		p = properties;
		readConfig();
	}

	protected abstract void readConfig() throws Exception;

	public abstract void start() throws Exception;

	public boolean isFirstAgent() {
		if (p == null) {
			JLG.debug("p is null");
		}
		String s = p.getProperty("server", "no");
		return s.equalsIgnoreCase("yes")
				&& p.getProperty("server.isFirstAgent", "no").equalsIgnoreCase(
						"yes");
	}

	public abstract void stop();





	public abstract boolean isConfigFilePresent();

	public abstract boolean allowsUserCreation();

	public abstract User login(String login, String password) throws Exception;

	public abstract void checkout(User user, String localDir) throws Exception;

	public abstract void commit(User user, String localDir) throws Exception;

	public abstract void mkdir(User user, String existingParentDir,
			String newDir) throws Exception;

	public abstract void rm(User user, String existingParentDir, String name)
			throws Exception;

	public abstract void rename(User user, String existingParentDir,
			String oldName, String newName) throws Exception;

	public abstract FileInterface getDir(User user, String dir)
			throws Exception;

	public abstract void checkout(User user, String remoteDir,
			String remoteFilename, File localDir) throws Exception;

	public abstract void commit(User user, String remoteDir, File file) throws Exception;

	public abstract void refreshContactList() throws Exception;

	public abstract Iterator<Contact> getContactIterator();

	public abstract String getProtocolName();

	public abstract String getName();

	public String getHelpURL() {
		return "http://code.google.com/p/ocp/wiki/Help";
	}

	public abstract boolean hasStorage();

	public abstract void removeStorage();

}
