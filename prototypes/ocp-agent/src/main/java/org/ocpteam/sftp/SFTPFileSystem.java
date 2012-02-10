package org.ocpteam.sftp;

import java.io.File;
import java.util.Vector;

import org.ocpteam.storage.FileInterface;
import org.ocpteam.storage.FileSystem;
import org.ocpteam.storage.User;

import com.jcraft.jsch.ChannelSftp.LsEntry;

public class SFTPFileSystem implements FileSystem {

	private SFTPAgent agent;
	private User user;

	public SFTPFileSystem(User user, SFTPAgent agent) {
		this.user = user;
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
		@SuppressWarnings("unchecked")
		Vector<LsEntry> v = (Vector<LsEntry>) agent.channel.ls(dir);
		SFTPFileImpl result = new SFTPFileImpl();
		for (int i = 0; i < v.size(); i++) {
			String name = v.get(i).getFilename();
			if (name.equals(".") || name.equals("..")) {
				continue;
			}
			result.add(new SFTPFileImpl(v.get(i).getFilename(), v.get(i).getAttrs().isDir()));
		}
		return result;
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

}
