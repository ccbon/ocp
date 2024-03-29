package org.ocpteamx.protocol.zip;

import java.io.File;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.ocpteam.core.Container;
import org.ocpteam.interfaces.IFile;
import org.ocpteam.interfaces.IFileSystem;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.LOG;

public class ZipFileSystem extends Container<ZipDataSource> implements
		IFileSystem {

	public ZipFileImpl root;

	public ZipFileSystem() {
	}

	ZipDataSource ds() {
		return getRoot();
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
		if (!remoteDir.endsWith("/")) {
			remoteDir += "/";
		}
		String path = remoteDir + remoteFilename;
		IFile file = getFile(path);
		if (file.isDirectory()) {
			File dir = new File(localDir, remoteFilename);
			JLG.mkdir(dir);
			IFile d = getFile(path);
			for (IFile child : d.listFiles()) {
				LOG.info("child: " + child.getName());
				checkout(path, child.getName(), dir);
			}
		} else { // file
			ZipUtils.extract(ds().getFile(), path.substring(1), new File(
					localDir, remoteFilename));
		}

	}

	@Override
	public void commit(String remoteDir, File file) throws Exception {
		LOG.info("zip commit: " + file.getName());
		if (remoteDir.startsWith("/")) {
			remoteDir = remoteDir.substring(1);
		}
		if (!remoteDir.endsWith("/")) {
			remoteDir += "/";
		}
		List<File> list = new LinkedList<File>();
		makeList(list, file);
		File[] files = list.toArray(new File[list.size()]);
		File parent = file.getParentFile();
		ZipUtils.add(ds().getFile(), remoteDir, parent, files);
		refresh();
	}

	private void makeList(List<File> list, File file) {
		list.add(file);
		if (file.isDirectory()) {
			for (File child : file.listFiles()) {
				makeList(list, child);
			}
		}
	}

	@Override
	public IFile getFile(String dir) throws Exception {
		refresh();
		IFile result = root.get(dir);
		if (result == null) {
			throw new Exception("File " + dir + " is null.");
		}
		return result;
	}

	@Override
	public void mkdir(String existingParentDir, String newDir) throws Exception {
		if (existingParentDir.startsWith("/")) {
			existingParentDir = existingParentDir.substring(1);
		}
		if (!existingParentDir.endsWith("/")) {
			existingParentDir += "/";
		}
		ZipUtils.mkdir(ds().getFile(), existingParentDir + newDir);
		refresh();
	}

	@Override
	public void rm(String existingParentDir, String name) throws Exception {
		if (existingParentDir.startsWith("/")) {
			existingParentDir = existingParentDir.substring(1);
		}
		if (!existingParentDir.endsWith("/")) {
			existingParentDir += "/";
		}
		ZipUtils.rm(ds().getFile(), existingParentDir + name);
		refresh();
	}

	@Override
	public void rename(String existingParentDir, String oldName, String newName)
			throws Exception {
		if (existingParentDir.startsWith("/")) {
			existingParentDir = existingParentDir.substring(1);
		}
		if (!existingParentDir.endsWith("/")) {
			existingParentDir += "/";
		}
		ZipUtils.rename(ds().getFile(), existingParentDir + oldName,
				existingParentDir + newName);
		refresh();

	}

	@Override
	public String getDefaultLocalDir() {
		return System.getProperty("user.home");
	}

	public void refresh() throws Exception {
		if (!ZipUtils.lock.tryLock()) {
			return;
		}
		this.root = new ZipFileImpl();
		ZipFile zip = null;
		try {
			zip = new ZipFile(ds().getFile());
			Enumeration<? extends ZipEntry> entries = zip.entries();
			while (entries.hasMoreElements()) {
				ZipEntry zipEntry = entries.nextElement();
				LOG.info("adding to fs " + zipEntry.getName());
				this.root.add(zipEntry.getName(), zipEntry);
			}
		} finally {
			if (zip != null) {
				zip.close();
			}
			ZipUtils.lock.unlock();
		}
	}

}
