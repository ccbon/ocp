package org.ocpteam.protocol.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;
import org.ocpteam.functionality.Authentication;
import org.ocpteam.layer.rsp.Agent;
import org.ocpteam.layer.rsp.Authenticable;
import org.ocpteam.layer.rsp.Context;
import org.ocpteam.layer.rsp.DataSource;
import org.ocpteam.layer.rsp.FileSystem;
import org.ocpteam.layer.rsp.User;
import org.ocpteam.misc.JLG;

public class FTPAgent extends Agent implements Authenticable {

	private String hostname;
	FTPClient ftp;

	public FTPAgent(DataSource ds) {
		super(ds);
		ftp = new FTPClient();
	}

	@Override
	protected void onConnect() throws Exception {

		hostname = ds.getURI().getHost();
		JLG.debug("hostname=" + hostname);
		try {
			ftp.connect(hostname);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Cannot connect to " + hostname);
		}
	}

	@Override
	protected void onDisconnect() {
		try {
			ftp.logout();
		} catch (IOException e) {
			// e.printStackTrace();
		}
	}

	@Override
	public void login() throws Exception {
		try {
			ftp.logout();
		} catch (Exception e) {
		}
		try {
			ftp.connect(hostname);
		} catch (Exception e) {
		}
		Authentication a = ds.designer.get(Authentication.class);
		String login = a.getLogin();
		String password = (String) a.getChallenge();
		if (ftp.login(login, password)) {
			JLG.debug("ftp logged in.");
			FTPUser user = new FTPUser(login, password,
					System.getProperty("user.home"));
			context = new Context(this, getFileSystem(user), "/");
			a.setUser(user);
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
	public FileSystem getFileSystem(User user) {
		return new FTPFileSystem((FTPUser) user, this);
	}

	@Override
	public void logout() throws Exception {
		ftp.logout();
	}

}
