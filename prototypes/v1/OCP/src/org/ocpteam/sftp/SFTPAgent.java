package org.ocpteam.sftp;

import java.io.File;
import java.util.Queue;

import org.ocpteam.ftp.FTPUser;
import org.ocpteam.misc.JLG;
import org.ocpteam.storage.Agent;
import org.ocpteam.storage.Contact;
import org.ocpteam.storage.FileSystem;
import org.ocpteam.storage.User;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

public class SFTPAgent extends Agent {

	private JSch jsch;
	Session session;
	private Channel channel;

	@Override
	public boolean requiresConfigFile() {
		return false;
	}

	@Override
	public boolean isConfigFilePresent() {
		return true;
	}

	@Override
	public File getConfigFile() {
		return null;
	}

	@Override
	protected void readConfig() throws Exception {
	}

	@Override
	public void start() throws Exception {
		jsch = new JSch();
		bIsStarted = true;
	}

	@Override
	public void stop() {
		jsch = null;
		bIsStarted = false;
	}

	@Override
	public boolean allowsUserCreation() {
		return false;
	}

	@Override
	public User login(String login, Object challenge) throws Exception {
		try {
			String[] array = login.split("@");
			String username = array[0];
			String hostname = array[1];
			int port = 22;
			int index = hostname.indexOf(':');
			if (index != -1) {
				port = Integer.parseInt(hostname.substring(index + 1));
			}
			session = jsch.getSession(username, hostname, port);
			
			SFTPUserInfo ui = new SFTPUserInfo();
			SSHChallenge c = (SSHChallenge) challenge;
			if (c.getType() == SSHChallenge.PASSWORD) {
				ui.setPassword(c.getPassword());
				session.setUserInfo(ui);
			} else if (c.getType() == SSHChallenge.PRIVATE_KEY) {
				if (c.getPassphrase() == null) {
					jsch.addIdentity(c.getPrivateKeyFile().getAbsolutePath());
				} else {
					jsch.addIdentity(c.getPrivateKeyFile().getAbsolutePath(), c.getPassphrase());
				}
			}
			
			session.connect();
			channel = session.openChannel("sftp");
			channel.connect();
			User user = new SFTPUser(login, c);
			return user;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Cannot login");
		}
	}

	@Override
	public void refreshContactList() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public Queue<Contact> makeContactQueue() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Contact toContact() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProtocolName() {
		return "SFTP";
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "SFTP-client";
	}

	@Override
	public boolean hasStorage() {
		return false;
	}

	@Override
	public void removeStorage() {
	}

	@Override
	public FileSystem getFileSystem(User user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean autoStarts() {
		return true;
	}
	
	@Override
	public boolean connectsWithSSH() {
		return true;
	}

}
