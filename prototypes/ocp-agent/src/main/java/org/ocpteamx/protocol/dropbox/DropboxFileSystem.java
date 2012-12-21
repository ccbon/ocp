package org.ocpteamx.protocol.dropbox;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.ocpteam.component.DSContainer;
import org.ocpteam.interfaces.IFile;
import org.ocpteam.interfaces.IFileSystem;
import org.ocpteam.misc.JLG;

import com.dropbox.client2.DropboxAPI.Entry;

public class DropboxFileSystem extends DSContainer<DropboxDataSource> implements
		IFileSystem {

	@Override
	public void checkoutAll(String localDir) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void commitAll(String localDir) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public IFile getFile(String dir) throws Exception {
		JLG.debug("About to get: " + dir);
		Entry entries = DropboxClient.mDBApi.metadata(dir, 100, null, true,
				null);

		DropboxFileImpl fi = new DropboxFileImpl();
		for (Entry e : entries.contents) {
			if (!e.isDeleted) {
				JLG.debug("File: " + e.path);
				fi.add(new DropboxFileImpl(e));
			}
		}

		return fi;
	}

	@Override
	public void mkdir(String existingParentDir, String newDir) throws Exception {
		if (!existingParentDir.endsWith("/")) {
			existingParentDir += "/";
		}
		DropboxClient.mDBApi.createFolder(existingParentDir + newDir);
	}

	@Override
	public void rm(String existingParentDir, String name) throws Exception {
		if (!existingParentDir.endsWith("/")) {
			existingParentDir += "/";
		}
		DropboxClient.mDBApi.delete(existingParentDir + name);
	}

	@Override
	public void rename(String existingParentDir, String oldName, String newName)
			throws Exception {
		if (!existingParentDir.endsWith("/")) {
			existingParentDir += "/";
		}
		DropboxClient.mDBApi.move(existingParentDir + oldName,
				existingParentDir + newName);
	}

	@Override
	public String getDefaultLocalDir() {
		return System.getProperty("user.home");
	}

	@Override
	public void checkout(String remoteDir, String remoteFilename,
			java.io.File localDir) throws Exception {
		JLG.debug("About to download \"" + remoteFilename + "\" from:"
				+ remoteDir);
		if (!remoteDir.endsWith("/")) {
			remoteDir += "/";
		}
		String path = localDir.getAbsolutePath();
		if (!path.endsWith("/")) {
			path += "/";
		}
		DropboxClient.mDBApi.getFile(remoteDir + remoteFilename, null,
				new FileOutputStream(path + remoteFilename), null);
	}

	@Override
	public void commit(String remoteDir, java.io.File file) throws Exception {
		JLG.debug("About to upload \"" + file.getName() + "\" to:" + remoteDir);
		if (!remoteDir.endsWith("/")) {
			remoteDir += "/";
		}
		if (file.isDirectory()) {
			mkdir(remoteDir, file.getName());
			for (java.io.File child : file.listFiles()) {
				JLG.debug("child: " + child.getName());
				commit(remoteDir + file.getName(), child);
			}
		} else {
			DropboxClient.mDBApi.putFile(remoteDir + file.getName(),
					new FileInputStream(file), file.length(), null, null);
		}
	}
}
