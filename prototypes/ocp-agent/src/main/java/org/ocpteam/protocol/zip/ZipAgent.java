package org.ocpteam.protocol.zip;

import java.io.FileInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.ocpteam.layer.rsp.Agent;
import org.ocpteam.layer.rsp.FileSystem;
import org.ocpteam.layer.rsp.User;
import org.ocpteam.misc.JLG;

public class ZipAgent extends Agent {

	public String zipfile;
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
		return false;
	}

	@Override
	public boolean isOnlyClient() {
		return true;
	}

	@Override
	public void connect() throws Exception {
		JLG.debug("opening datasource: " + zipfile);
		createZipFileSystem(zipfile);
		bIsConnected = true;
	}

	private void createZipFileSystem(String zipfile) throws Exception {
		fs = new ZipFileSystem(this);
		ZipInputStream zipInputStream = null;
		try {
			zipInputStream = new ZipInputStream(new FileInputStream(zipfile));
			ZipEntry zipEntry = null;

			while ((zipEntry = zipInputStream.getNextEntry()) != null) {
				JLG.debug("adding to fs " + zipEntry.getName());
				fs.root.add(zipEntry.getName(), zipEntry);
			}

		} finally {
			if (zipInputStream != null) {
				zipInputStream.close();
			}
		}
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
