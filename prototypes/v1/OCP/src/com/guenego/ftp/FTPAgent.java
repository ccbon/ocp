package com.guenego.ftp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import com.guenego.misc.JLG;
import com.guenego.storage.Agent;
import com.guenego.storage.User;

public class FTPAgent extends Agent {

	public static final File configFile = new File("ftp.properties");
	private String hostname;
	private FTPClient ftp;

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

	@Override
	public void checkout(User user, String localDir) throws Exception {
		// make sure you are at the top directory
		reconnect((FTPUser) user);
		// now remove the local dir
		JLG.rm(localDir);
		JLG.mkdir(localDir);
		checkout("/", new File(localDir));

	}

	private void checkout(String remotePath, File localDir) throws Exception {
		ftp.changeWorkingDirectory(remotePath);
		FileOutputStream fos = null;
		FTPFile[] ftpFiles = ftp.listFiles();
		for (FTPFile ftpFile : ftpFiles) {
			// Check if FTPFile is a regular file
			if (ftpFile.isFile()) {
				JLG.debug("FTPFile: " + ftpFile.getName() + "; "
						+ ftpFile.getSize());
				fos = new FileOutputStream(
						new File(localDir, ftpFile.getName()));
				ftp.retrieveFile(ftpFile.getName(), fos);
				fos.close();
			} else if (ftpFile.isDirectory()) {
				JLG.mkdir(new File(localDir, ftpFile.getName()));
				checkout(remotePath + ftpFile.getName() + "/", new File(
						localDir, ftpFile.getName()));
			}
		}
		ftp.changeWorkingDirectory(remotePath);
	}

	private void reconnect(FTPUser ftpUser) throws IOException {
		ftp.logout();
		ftp.connect(hostname);
		JLG.debug("login=" + ftpUser.getLogin() + " password="
				+ ftpUser.getPassword());
		ftp.login(ftpUser.getLogin(), ftpUser.getPassword());

	}

}
