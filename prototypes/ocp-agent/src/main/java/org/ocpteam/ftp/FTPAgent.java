package org.ocpteam.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;
import org.ocpteam.layer.rsp.Agent;
import org.ocpteam.layer.rsp.FileSystem;
import org.ocpteam.layer.rsp.User;
import org.ocpteam.misc.JLG;

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
	public User login(String login, Object challenge) throws Exception {
		String password = (String) challenge;
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

	void commit(User user, File file, String remotePath) throws Exception {
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
	public FileSystem getFileSystem(User user) {
		return new FTPFileSystem((FTPUser) user, this);
	}

	@Override
	public boolean autoStarts() {
		return false;
	}

	@Override
	public void logout(User user) throws Exception {
		ftp.logout();
	}

	@Override
	public boolean isOnlyClient() {
		return true;
	}

}
