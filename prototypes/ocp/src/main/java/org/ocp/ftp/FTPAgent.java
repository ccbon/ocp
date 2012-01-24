package org.ocp.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Queue;

import org.apache.commons.net.ftp.FTPClient;

import org.ocp.misc.JLG;
import org.ocp.misc.URL;
import org.ocp.storage.Agent;
import org.ocp.storage.Contact;
import org.ocp.storage.FileSystem;
import org.ocp.storage.User;

public class FTPAgent extends Agent {

	public static final File configFile = new File("ftp.properties");
	private String hostname;
	FTPClient ftp;

	public FTPAgent() {
		ftp = new FTPClient();
	}

	@Override
	public File getConfigFile() {
		return configFile;
	}

	@Override
	protected void readConfig() throws Exception {
		this.hostname = p.getProperty("hostname", "ftp.guenego.com");
		if (p.getProperty("debug", "true").equalsIgnoreCase("true")) {
			JLG.debug_on();
		}

	}

	@Override
	public void start() throws Exception {
		ftp.connect(hostname);
		FTPContact c = new FTPContact("FTP server");
		c.addURL(new URL("ftp", hostname, 21));
		addContact(c);
		FTPContact myself = (FTPContact) toContact();
		addContact(myself);
		bIsStarted = true;
	}

	@Override
	public void stop() {
		try {
			ftp.logout();
			bIsStarted = false;
		} catch (IOException e) {
			// e.printStackTrace();
		}
	}

	@Override
	public boolean isConfigFilePresent() {
		return configFile.isFile();
	}

	@Override
	public boolean allowsUserCreation() {
		return false;
	}

	@Override
	public User login(String login, String password) throws Exception {
		if (ftp.login(login, password)) {
			JLG.debug("ftp logged in.");
			FTPUser user = new FTPUser(login, password, p.getProperty(
					"default.dir", System.getProperty("user.home")));
			return user;
		} else {
			throw new Exception("Cannot Login.");
		}
	}




	void reconnect(FTPUser ftpUser) throws IOException {
		ftp.logout();
		ftp.connect(hostname);
		JLG.debug("login=" + ftpUser.getLogin() + " password="
				+ ftpUser.getPassword());
		ftp.login(ftpUser.getLogin(), ftpUser.getPassword());
	}


	void commit(User user, File file, String remotePath)
			throws Exception {
		JLG.debug("about to commit " + file.getName());

		for (File child : file.listFiles()) {
			JLG.debug("child: " + child.getName());

			if (child.isDirectory()) {
				ftp.mkd(remotePath + child.getName() + "/");
				commit(user, child, remotePath + child.getName() + "/");
			} else {
				FileInputStream fis = new FileInputStream(child);
				ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
				ftp.storeFile(remotePath + child.getName(), fis);
				fis.close();
			}
		}
	}

	@Override
	public void refreshContactList() throws Exception {
		// we don't do anything.

	}

	@Override
	public String getProtocolName() {
		return "FTP";
	}

	@Override
	public String getName() {
		return "client";
	}

	@Override
	public String getHelpURL() {
		return "http://code.google.com/p/ocp/wiki/FTPHelp";
	}

	@Override
	public boolean hasStorage() {
		return false;
	}

	@Override
	public void removeStorage() {
	}

	@Override
	public Queue<Contact> makeContactQueue() throws Exception {
		throw new Exception(
				"this function is normally useless or may be it has to be implemented...");
	}

	@Override
	public Contact toContact() {
		return new FTPContact("myself (the client)");
	}

	@Override
	public FileSystem getFileSystem(User user) {
		return new FTPFileSystem((FTPUser) user, this);
	}

	@Override
	public boolean autoStarts() {
		return false;
	}

}
