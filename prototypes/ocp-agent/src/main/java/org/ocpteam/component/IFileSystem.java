package org.ocpteam.component;

import java.io.File;


public interface IFileSystem extends IDataModel {

	public void checkoutAll(String localDir) throws Exception;
	
	public void commitAll(String localDir) throws Exception;
	
	public void checkout(String remoteDir,
			String remoteFilename, File localDir) throws Exception;

	public void commit(String remoteDir, File file) throws Exception;

	public IFile getFile(String dir)
			throws Exception;

	public void mkdir(String existingParentDir,
			String newDir) throws Exception;
	
	public void rm(String existingParentDir, String name)
			throws Exception;

	public void rename(String existingParentDir,
			String oldName, String newName) throws Exception;

	public String getDefaultLocalDir();
}
