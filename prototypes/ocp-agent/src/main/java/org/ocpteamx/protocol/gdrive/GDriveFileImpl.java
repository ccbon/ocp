package org.ocpteamx.protocol.gdrive;

import java.util.Collection;
import java.util.HashSet;

import org.ocpteam.interfaces.IFile;

import com.google.api.services.drive.model.File;

public class GDriveFileImpl implements IFile {
	private HashSet<GDriveFileImpl> set;
	private File f;

	public GDriveFileImpl(File f) {
		this.f = f;
	}

	public GDriveFileImpl() {
		set = new HashSet<GDriveFileImpl>();
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
		return f.getMimeType().equals("application/vnd.google-apps.folder");
	}

	@Override
	public String getName() {
		return f.getTitle();
	}

	public void add(GDriveFileImpl gDriveFileImpl) {
		set.add(gDriveFileImpl);
	}

	@Override
	public long getSize() {
		return f.getFileSize();
	}

}
