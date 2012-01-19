package com.guenego.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import com.guenego.misc.JLG;
import com.guenego.storage.Agent;
import com.guenego.storage.FileInterface;
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
	public void stop() {
		try {
			ftp.logout();
		} catch (IOException e) {
			e.printStackTrace();
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

	@Override
	public void commit(User user, String localDir) throws Exception {

		// make sure you are at the top directory
		reconnect((FTPUser) user);
		ftp.removeDirectory("/");
		File file = new File(localDir);
		if (!file.isDirectory()) {
			throw new Exception("must be a directory:" + localDir);
		}
		commit(user, file, "/");
	}

	private void commit(User user, File file, String remotePath)
			throws Exception {
		JLG.debug("about to commit " + file.getName());

		for (File child : file.listFiles()) {
			JLG.debug("child: " + child.getName());

			if (child.isDirectory()) {
				ftp.mkd(remotePath + child.getName() + "/");
				commit(user, child, remotePath + child.getName() + "/");
			} else {
				FileInputStream fis = new FileInputStream(child);
				ftp.storeFile(remotePath + child.getName(), fis);
			}
		}
	}

	@Override
	public void mkdir(User user, String existingParentDir, String newDir)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void rm(User user, String existingParentDir, String name)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void rename(User user, String existingParentDir, String oldName,
			String newName) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public FileInterface getDir(User user, String dir) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void checkout(User user, String remoteDir, String remoteFilename,
			File localDir) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void commit(User user, String remoteDir, File file) throws Exception {
		// TODO Auto-generated method stub
		
	}

}