package org.ocpteam.layer.rsp;

import java.io.File;

public interface FileSystem extends DataModel {

	public void checkoutAll(String localDir) throws Exception;
	
	public void commitAll(String localDir) throws Exception;
	
	public void checkout(String remoteDir,
			String remoteFilename, File localDir) throws Exception;

	public void commit(String remoteDir, File file) throws Exception;

	public FileInterface getFile(String dir)
			throws Exception;

	public void mkdir(String existingParentDir,
			String newDir) throws Exception;
	
	public void rm(String existingParentDir, String name)
			throws Exception;

	public void rename(String existingParentDir,
			String oldName, String newName) throws Exception;

	public String getDefaultLocalDir();
}