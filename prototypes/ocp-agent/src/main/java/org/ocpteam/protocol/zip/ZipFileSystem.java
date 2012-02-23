package org.ocpteam.protocol.zip;

import java.io.File;

import org.ocpteam.layer.rsp.FileInterface;
import org.ocpteam.layer.rsp.FileSystem;

public class ZipFileSystem implements FileSystem {

	private ZipAgent agent;

	public ZipFileSystem(ZipAgent agent) {
		this.agent = agent;
	}

	@Override
	public void checkoutAll(String localDir) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void commitAll(String localDir) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void checkout(String remoteDir, String remoteFilename, File localDir)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void commit(String remoteDir, File file) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public FileInterface getDir(String dir) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void mkdir(String existingParentDir, String newDir) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void rm(String existingParentDir, String name) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void rename(String existingParentDir, String oldName, String newName)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public String getDefaultLocalDir() {
		return System.getProperty("user.home");
	}

}
