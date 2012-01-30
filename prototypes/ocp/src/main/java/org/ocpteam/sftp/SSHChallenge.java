package org.ocpteam.sftp;

import java.io.File;

public class SSHChallenge {

	public static final int PASSWORD = 0;
	public static final int PRIVATE_KEY = 1;
	private int type;
	private String password;
	private File privateKeyFile;
	private String defaultLocalDir;

	public int getType() {
		return type;
	}

	public String getPassword() {
		return password;
	}

	public File getPrivateKeyFile() {
		return privateKeyFile;
	}

	public String getPassphrase() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getDefaultLocalDir() {
		return defaultLocalDir;
	}

	public void setPassword(String password) {
		this.password = password;
		
	}

	public void setDefaultLocalDir(String dir) {
		this.defaultLocalDir = dir;
		
	}

	public void setPrivateKeyFile(File f) {
		this.privateKeyFile = f;
		
	}

	public void setType(int type) {
		this.type = type;
	}

}
