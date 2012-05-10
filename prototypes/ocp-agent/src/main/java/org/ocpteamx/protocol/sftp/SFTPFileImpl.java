package org.ocpteamx.protocol.sftp;

import java.util.Collection;
import java.util.HashSet;

import org.ocpteam.interfaces.IFile;

public class SFTPFileImpl implements IFile {


	private HashSet<SFTPFileImpl> set;
	private boolean bIsDirectory;
	private String name;

	public SFTPFileImpl() {
		set = new HashSet<SFTPFileImpl>();
	}

	public SFTPFileImpl(String filename, boolean dir) {
		this.name = filename;
		this.bIsDirectory = dir;
	}

	@Override
	public Collection<? extends IFile> listFiles() {
		return set;
	}

	@Override
	public boolean isFile() {
		return !bIsDirectory;
	}

	@Override
	public boolean isDirectory() {
		return bIsDirectory;
	}

	@Override
	public String getName() {
		return name;
	}
	
	public void add(SFTPFileImpl sftpFileImpl) {
		set.add(sftpFileImpl);
	}


}
