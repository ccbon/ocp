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

	private static final File configFile = new File("sftp.properties");
	private String hostname;
	private JSch jsch;
	Session session;
	private int port;
	private Channel channel;

	@Override
	public boolean requiresConfigFile() {
		return false;
	}

	@Override
	public boolean isConfigFilePresent() {
		return configFile.isFile();
	}

	@Override
	public File getConfigFile() {
		return configFile;
	}

	@Override
	protected void readConfig() throws Exception {
		this.hostname = p.getProperty("hostname", "localhost");
		this.port = Integer.parseInt(p.getProperty("port", "22"));
		if (p.getProperty("debug", "true").equalsIgnoreCase("true")) {
			JLG.debug_on();
		}
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public User login(String login, String password) throws Exception {
		try {
			session = jsch.getSession(login, hostname, port);
			
			UserInfo ui = new SFTPUserInfo(password);
			session.setUserInfo(ui);
			session.connect();
			channel = session.openChannel("sftp");
			channel.connect();
			FTPUser user = new FTPUser(login, password, p.getProperty(
					"default.dir", System.getProperty("user.home")));
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
