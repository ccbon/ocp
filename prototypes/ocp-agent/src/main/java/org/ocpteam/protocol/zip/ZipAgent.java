package org.ocpteam.protocol.zip;

import java.io.File;

import org.ocpteam.layer.rsp.Agent;
import org.ocpteam.layer.rsp.Authentication;
import org.ocpteam.layer.rsp.Context;
import org.ocpteam.layer.rsp.DataSource;
import org.ocpteam.layer.rsp.FileSystem;
import org.ocpteam.layer.rsp.User;
import org.ocpteam.misc.JLG;

public class ZipAgent extends Agent {

	public File zipfile;
	private ZipFileSystem fs;

	public ZipAgent(DataSource ds) {
		super(ds);
	}

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
	public boolean isOnlyClient() {
		return true;
	}

	@Override
	protected void onConnect() throws Exception {
		zipfile = ds.getFile();
		JLG.debug("opening datasource: " + zipfile);
		createZipFileSystem();
		context = new Context(this, fs, "/");
	}

	private void createZipFileSystem() throws Exception {
		fs = new ZipFileSystem(this);
		fs.refresh();
	}

	@Override
	protected void onDisconnect() {
	}

	@Override
	public void login(Authentication a) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void logout(Authentication a) throws Exception {
		// TODO Auto-generated method stub
	}

}
