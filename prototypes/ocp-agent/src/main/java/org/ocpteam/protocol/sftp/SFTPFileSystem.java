package org.ocpteam.protocol.sftp;

import java.io.File;
import java.io.FileInputStream;
import java.util.Vector;

import org.ocpteam.component.DataSource;
import org.ocpteam.component.FileSystem;
import org.ocpteam.core.IContainer;
import org.ocpteam.layer.rsp.FileInterface;
import org.ocpteam.layer.rsp.User;
import org.ocpteam.misc.JLG;

import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.SftpATTRS;

public class SFTPFileSystem implements FileSystem {

	private SFTPClient agent;
	protected User user;
	protected DataSource ds;

	public SFTPFileSystem(User user, SFTPClient agent) {
		this.user = user;
		this.agent = agent;
	}

	@Override
	public void checkoutAll(String localDir) throws Exception {
		throw new Exception("volontary not implemented for security reason.");
	}

	@Override
	public void commitAll(String localDir) throws Exception {
		throw new Exception("volontary not implemented for security reason.");
	}

	@Override
	public void checkout(String remoteDir, String remoteFilename, File localDir)
			throws Exception {
		if (!remoteDir.endsWith("/")) {
			remoteDir += "/";
		}
		String path = remoteDir + remoteFilename;
		SftpATTRS attr = agent.channel.lstat(path);
		if (attr.isDir()) {
			File dir = new File(localDir, remoteFilename);
			JLG.mkdir(dir);
			FileInterface d = getFile(path);
			for (FileInterface child : d.listFiles()) {
				JLG.debug("child: " + child.getName());
				checkout(path, child.getName(), dir);
			}
		} else { // file
			File f = new File(localDir, remoteFilename);
			agent.channel.get(path, f.getAbsolutePath());
		}
	}

	@Override
	public void commit(String remoteDir, File file) throws Exception {
		if (!remoteDir.endsWith("/")) {
			remoteDir += "/";
		}
		if (file.isDirectory()) {
			mkdir(remoteDir, file.getName());
			for (File child : file.listFiles()) {
				JLG.debug("child: " + child.getName());
				commit(remoteDir + file.getName(), child);
			}
		} else {
			FileInputStream fis = new FileInputStream(file);
			agent.channel.put(fis, remoteDir + file.getName());
			fis.close();
		}

	}

	@Override
	public FileInterface getFile(String dir) throws Exception {
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
		if (!existingParentDir.endsWith("/")) {
			existingParentDir += "/";
		}
		agent.channel.mkdir(existingParentDir + newDir);
	}

	@Override
	public void rm(String existingParentDir, String name) throws Exception {
		if (!existingParentDir.endsWith("/")) {
			existingParentDir += "/";
		}
		String path = existingParentDir + name;
		SftpATTRS attr = agent.channel.lstat(path);
		if (attr.isDir()) {
			agent.channel.rmdir(path);
		} else {
			agent.channel.rm(path);
		}
	}

	@Override
	public void rename(String existingParentDir, String oldName, String newName)
			throws Exception {
		if (!existingParentDir.endsWith("/")) {
			existingParentDir += "/";
		}
		agent.channel.rename(existingParentDir + oldName, existingParentDir + newName);
	}

	@Override
	public String getDefaultLocalDir() {
		return user.getDefaultLocalDir();
	}

	@Override
	public void setParent(IContainer parent) {
		this.ds = (DataSource) parent;
	}

}
