package com.guenego.ftp;

import java.io.File;

import org.apache.commons.net.ftp.FTPClient;

import com.guenego.ocp.Agent;
import com.guenego.ocp.Contact;

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

	}

	@Override
	public void start() throws Exception {
		ftp.connect(hostname);
	}


	@Override
	public boolean isConfigFilePresent() {
		return configFile.isFile();
	}

}
