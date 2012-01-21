package com.guenego.storage;

import java.io.File;

public interface FileSystem {

	public void checkoutAll(String localDir) throws Exception;
	
	public void commitAll(String localDir) throws Exception;
	
	public void checkout(String remoteDir,
			String remoteFilename, File localDir) throws Exception;

	public void commit(String remoteDir, File file) throws Exception;

	public FileInterface getDir(String dir)
			throws Exception;

	public void mkdir(String existingParentDir,
			String newDir) throws Exception;
	
	public void rm(String existingParentDir, String name)
			throws Exception;

	public void rename(String existingParentDir,
			String oldName, String newName) throws Exception;
}
