package org.ocpteamx.protocol.dropbox;

import java.util.Collection;
import java.util.HashSet;

import org.ocpteam.interfaces.IFile;

import com.dropbox.client2.DropboxAPI.Entry;

public class DropboxFileImpl implements IFile {
	private HashSet<DropboxFileImpl> set;
	private Entry f;

	public DropboxFileImpl(Entry f) {
		this.f = f;
	}

	public DropboxFileImpl() {
		set = new HashSet<DropboxFileImpl>();
	}

	@Override
	public Collection<? extends IFile> listFiles() {
		return set;
	}

	@Override
	public boolean isFile() {
		return !isDirectory();
	}

	@Override
	public boolean isDirectory() {
		return f.isDir;
	}

	@Override
	public String getName() {
		return f.fileName();
	}

	public void add(DropboxFileImpl dropboxFileImpl) {
		set.add(dropboxFileImpl);
	}
	
	@Override
	public long getSize() {
		return f.bytes;
	}

}
