package org.ocpteam.protocol.zip;

import java.io.File;
import java.io.FileInputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.ocpteam.design.Container;
import org.ocpteam.layer.rsp.DataSource;
import org.ocpteam.layer.rsp.FileInterface;
import org.ocpteam.layer.rsp.FileSystem;
import org.ocpteam.misc.JLG;

public class ZipFileSystem implements FileSystem {

	private ZipAgent agent;
	public ZipFileImpl root;
	protected DataSource ds;

	public ZipFileSystem(ZipAgent agent) {
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
		if (!remoteDir.endsWith("/")) {
			remoteDir += "/";
		}
		String path = remoteDir + remoteFilename;
		FileInterface file = getFile(path);
		if (file.isDirectory()) {
			File dir = new File(localDir, remoteFilename);
			JLG.mkdir(dir);
			FileInterface d = getFile(path);
			for (FileInterface child : d.listFiles()) {
				JLG.debug("child: " + child.getName());
				checkout(path, child.getName(), dir);
			}
		} else { // file
			ZipUtils.extract(agent.ds.getFile(), path.substring(1), new File(localDir, remoteFilename));
		}


	}

	@Override
	public void commit(String remoteDir, File file) throws Exception {
		JLG.debug("zip commit: " + file.getName());
		if (remoteDir.startsWith("/")) {
			remoteDir = remoteDir.substring(1);
		}
		if (!remoteDir.endsWith("/")) {
			remoteDir += "/";
		}
		List<File> list = new LinkedList<File>();
		makeList(list, file);
		File[] files = (File[]) list.toArray(new File[list.size()]);
		File parent = file.getParentFile();
		ZipUtils.add(agent.ds.getFile(), remoteDir, parent, files);
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
	public FileInterface getFile(String dir) throws Exception {
		refresh();
		return root.get(dir);
	}

	@Override
	public void mkdir(String existingParentDir, String newDir) throws Exception {
		if (existingParentDir.startsWith("/")) {
			existingParentDir = existingParentDir.substring(1);
		}
		if (!existingParentDir.endsWith("/")) {
			existingParentDir += "/";
		}
		ZipUtils.mkdir(agent.ds.getFile(), existingParentDir + newDir);
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
		ZipUtils.rm(agent.ds.getFile(), existingParentDir + name);
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
		ZipUtils.rename(agent.ds.getFile(), existingParentDir + oldName, existingParentDir + newName);
		refresh();

	}

	@Override
	public String getDefaultLocalDir() {
		return System.getProperty("user.home");
	}

	public void refresh() throws Exception {
		this.root = new ZipFileImpl();
		ZipInputStream zipInputStream = null;
		try {
			zipInputStream = new ZipInputStream(new FileInputStream(agent.ds.getFile()));
			ZipEntry zipEntry = null;

			while ((zipEntry = zipInputStream.getNextEntry()) != null) {
				JLG.debug("adding to fs " + zipEntry.getName());
				this.root.add(zipEntry.getName(), zipEntry);
			}

		} finally {
			if (zipInputStream != null) {
				zipInputStream.close();
			}
		}
	}

	@Override
	public void setParent(Container parent) {
		this.ds = (DataSource) parent;
	}



}
