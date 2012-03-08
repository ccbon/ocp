package org.ocpteam.protocol.zip;

import org.ocpteam.layer.rsp.Agent;
import org.ocpteam.layer.rsp.Context;
import org.ocpteam.layer.rsp.DataSource;
import org.ocpteam.layer.rsp.FileSystem;
import org.ocpteam.layer.rsp.User;
import org.ocpteam.misc.JLG;

public class ZipAgent extends Agent {

	private ZipFileSystem fs;

	public ZipAgent(DataSource ds) {
		super(ds);
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
		JLG.debug("opening datasource: " + ds.getFile());
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

}
