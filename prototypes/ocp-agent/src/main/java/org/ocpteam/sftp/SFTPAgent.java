package org.ocpteam.sftp;

import java.io.File;

import org.ocpteam.layer.rsp.Agent;
import org.ocpteam.layer.rsp.FileSystem;
import org.ocpteam.layer.rsp.User;
import org.ocpteam.misc.JLG;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class SFTPAgent extends Agent {

	private JSch jsch;
	Session session;
	public ChannelSftp channel;

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
					JLG.debug("passphrase is null");
					jsch.addIdentity(c.getPrivateKeyFile().getAbsolutePath());
				} else {
					JLG.debug("passphrase is set");
					jsch.addIdentity(c.getPrivateKeyFile().getAbsolutePath(), c.getPassphrase());
				}
				session.setUserInfo(ui);
			}
			
			session.connect();
			channel = (ChannelSftp) session.openChannel("sftp");
			channel.connect();
			User user = new SFTPUser(login, c);
			return user;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Cannot login");
		}
	}

	@Override
	public String getProtocolName() {
		return "SFTP";
	}

	@Override
	public String getName() {
		return "SFTP-client";
	}

	@Override
	public FileSystem getFileSystem(User user) {
		return new SFTPFileSystem(user, this);
	}

	@Override
	public boolean autoStarts() {
		return true;
	}
	
	@Override
	public boolean connectsWithSSH() {
		return true;
	}

	@Override
	public void logout(User user) throws Exception {
		channel.exit();
		session.disconnect();
	}

	@Override
	public boolean isOnlyClient() {
		return true;
	}

}
