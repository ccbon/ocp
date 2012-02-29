package org.ocpteam.protocol.zip;

import java.io.File;

import org.ocpteam.layer.rsp.Agent;
import org.ocpteam.layer.rsp.Context;
import org.ocpteam.layer.rsp.FileSystem;
import org.ocpteam.layer.rsp.User;
import org.ocpteam.misc.JLG;

public class ZipAgent extends Agent {

	public File zipfile;
	private ZipFileSystem fs;

	@Override
	public String getProtocolName() {
		return "ZIP";
	}

	@Override
	public String getName() {
		return "Zip client";
	}

	@Override
	public FileSystem getFileSystem(User user) {

		return fs;
	}

	@Override
	public boolean autoConnect() {
		return true;
	}

	@Override
	public boolean isOnlyClient() {
		return true;
	}

	@Override
	public void connect() throws Exception {
		zipfile = ds.getFile();
		JLG.debug("opening datasource: " + zipfile);
		createZipFileSystem();
		initialContext = new Context(this, fs, "/");
		bIsConnected = true;
	}

	private void createZipFileSystem() throws Exception {
		fs = new ZipFileSystem(this);
		fs.refresh();
	}

	@Override
	public void disconnect() {
		bIsConnected = false;
	}

	@Override
	public boolean allowsUserCreation() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public User login(String login, Object challenge) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void logout(User user) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean usesAuthentication() {
		return false;
	}

}
