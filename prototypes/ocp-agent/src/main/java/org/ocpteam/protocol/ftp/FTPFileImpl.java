package org.ocpteam.protocol.ftp;

import java.util.Collection;
import java.util.HashSet;

import org.apache.commons.net.ftp.FTPFile;
import org.ocpteam.interfaces.IFile;


public class FTPFileImpl implements IFile {

	private HashSet<FTPFileImpl> set;
	private FTPFile ftpFile;

	public FTPFileImpl() {
		set = new HashSet<FTPFileImpl>();
	}
	public FTPFileImpl(FTPFile ftpFile) {
		this.ftpFile = ftpFile;
	}
	@Override
	public Collection<? extends IFile> listFiles() {
		return set;
	}

	@Override
	public boolean isFile() {
		return ftpFile.isFile();
	}

	@Override
	public boolean isDirectory() {
		return ftpFile.isDirectory();
	}

	@Override
	public String getName() {
		return ftpFile.getName();
	}

	public void add(FTPFileImpl ftpFileImpl) {
		set.add(ftpFileImpl);
	}

}
