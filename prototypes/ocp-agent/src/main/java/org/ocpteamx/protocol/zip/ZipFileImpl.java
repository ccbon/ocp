package org.ocpteamx.protocol.zip;

import java.util.Collection;
import java.util.HashMap;
import java.util.zip.ZipEntry;

import org.ocpteam.interfaces.IFile;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.LOG;

public class ZipFileImpl implements IFile {

	private String path;
	private boolean bIsDirectory;
	private HashMap<String, ZipFileImpl> map;
	private long size;

	public ZipFileImpl(ZipEntry zipEntry) {
		map = new HashMap<String, ZipFileImpl>();
		this.path = zipEntry.getName();
		LOG.debug("zipentry.path=" + path);
		this.bIsDirectory = zipEntry.isDirectory();
		this.size = zipEntry.getSize();
	}

	// root case
	public ZipFileImpl() {
		map = new HashMap<String, ZipFileImpl>();
	}

	@Override
	public Collection<? extends IFile> listFiles() {
		return map.values();
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
		return JLG.basename(path);
	}

	public void add(ZipFileImpl zipFileImpl) {
		map.put(zipFileImpl.getName(), zipFileImpl);
	}

	public void add(String path, ZipEntry zipEntry) {
		if (path.startsWith("/")) {
			path = path.substring(1);
		}
		if (path.endsWith("/")) {
			path = path.substring(0, path.length() - 1);
		}
		int index = path.indexOf("/");
		if (index == -1) {
			add(new ZipFileImpl(zipEntry));
		} else {
			String dir = path.substring(0, index);
			if (!map.containsKey(dir)) {
				map.put(dir, new ZipFileImpl());
			}
			map.get(dir).add(path.substring(index + 1), zipEntry);
		}
	}

	public ZipFileImpl get(String path) {
		if (path.equals("/")) {
			return this;
		}
		if (path.startsWith("/")) {
			path = path.substring(1);
		}
		if (path.endsWith("/")) {
			path = path.substring(0, path.length() - 1);
		}
		int index = path.indexOf("/");
		if (index == -1) {
			return map.get(path);
		} else {
			String dir = path.substring(0, index);
			return map.get(dir).get(path.substring(index + 1));
		}
	}

	@Override
	public long getSize() {
		return size;
	}

	// public int getSize(String filePath) {
	// return get(filePath).
	// }

}
