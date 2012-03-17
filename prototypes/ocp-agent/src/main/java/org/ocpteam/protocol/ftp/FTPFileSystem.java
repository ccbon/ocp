package org.ocpteam.protocol.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.ocpteam.component.DataSource;
import org.ocpteam.component.IFileSystem;
import org.ocpteam.component.IFile;
import org.ocpteam.core.IContainer;
import org.ocpteam.misc.JLG;

public class FTPFileSystem implements IFileSystem {

	private org.apache.commons.net.ftp.FTPClient ftp;
	protected DataSource ds;

	public FTPFileSystem(FTPClient agent) {
		this.ftp = agent.ftp;
	}

	public FTPFileSystem() {
	}

	@Override
	public void checkoutAll(String localDir) throws Exception {
	}

	@Override
	public void commitAll(String localDir) throws Exception {
	}

	@Override
	public void checkout(String remoteDir, String remoteFilename, File localDir)
			throws Exception {
		if (!remoteDir.endsWith("/")) {
			remoteDir += "/";
		}
		final String filename = remoteFilename;
		FileOutputStream fos = null;

		FTPFile[] ftpFiles = ftp.listFiles(remoteDir, new FTPFileFilter() {

			@Override
			public boolean accept(FTPFile arg0) {
				return arg0.getName().equals(filename);
			}
		});
		if (ftpFiles.length != 1) {
			throw new Exception("ftpFiles.length != 1");
		}
		FTPFile ftpFile = ftpFiles[0];

		// Check if FTPFile is a regular file
		if (ftpFile.isFile()) {
			JLG.debug("FTPFile: " + ftpFile.getName() + "; "
					+ ftpFile.getSize());
			fos = new FileOutputStream(new File(localDir, ftpFile.getName()));
			ftp.retrieveFile(ftpFile.getName(), fos);
			fos.close();
		} else if (ftpFile.isDirectory()) {
			File dir = new File(localDir, ftpFile.getName());
			JLG.mkdir(dir);
			checkoutDir(remoteDir + remoteFilename + "/", dir);
		}

	}

	private void checkoutDir(String remotePath, File localDir) throws Exception {
		ftp.changeWorkingDirectory(remotePath);
		FileOutputStream fos = null;
		FTPFile[] ftpFiles = ftp.listFiles();
		for (FTPFile ftpFile : ftpFiles) {
			// Check if FTPFile is a regular file
			if (ftpFile.isFile()) {
				JLG.debug("FTPFile: " + ftpFile.getName() + "; "
						+ ftpFile.getSize());
				fos = new FileOutputStream(
						new File(localDir, ftpFile.getName()));
				ftp.retrieveFile(ftpFile.getName(), fos);
				fos.close();
			} else if (ftpFile.isDirectory()) {
				JLG.mkdir(new File(localDir, ftpFile.getName()));
				checkoutDir(remotePath + ftpFile.getName() + "/", new File(
						localDir, ftpFile.getName()));
			}
		}
	}

	@Override
	public void commit(String remoteDir, File file) throws Exception {
		if (!remoteDir.endsWith("/")) {
			remoteDir += "/";
		}
		if (file.isDirectory()) {
			ftp.mkd(remoteDir + file.getName());
			for (File child : file.listFiles()) {
				JLG.debug("child: " + child.getName());
				commit(remoteDir + file.getName(), child);
			}
		} else {
			FileInputStream fis = new FileInputStream(file);
			ftp.setFileType(org.apache.commons.net.ftp.FTPClient.BINARY_FILE_TYPE);
			ftp.storeFile(remoteDir + file.getName(), fis);
			fis.close();
		}

	}

	@Override
	public IFile getFile(String dir) throws Exception {
		if (!ftp.changeWorkingDirectory(dir)) {
			throw new Exception(dir + " is not a remote directory");
		}
		FTPFileImpl result = new FTPFileImpl();
		FTPFile[] ftpFiles = ftp.listFiles();
		for (FTPFile ftpFile : ftpFiles) {
			result.add(new FTPFileImpl(ftpFile));
		}
		return result;
	}

	@Override
	public void mkdir(String existingParentDir, String newDir) throws Exception {
		if (!existingParentDir.endsWith("/")) {
			existingParentDir += "/";
		}
		ftp.mkd(existingParentDir + "/" + newDir);

	}

	@Override
	public void rm(String existingParentDir, String name) throws Exception {
		if (!existingParentDir.endsWith("/")) {
			existingParentDir += "/";
		}
		final String fname = name;
		FTPFile[] ftpFiles = ftp.listFiles(existingParentDir,
				new FTPFileFilter() {

					@Override
					public boolean accept(FTPFile arg0) {
						return arg0.getName().equals(fname);
					}
				});
		if (ftpFiles.length != 1) {
			throw new Exception("ftpFiles.length != 1");
		}
		FTPFile ftpFile = ftpFiles[0];

		// Check if FTPFile is a regular file
		if (ftpFile.isFile()) {
			ftp.deleteFile(existingParentDir + name);
		} else if (ftpFile.isDirectory()) {
			// go in the directory and remove recursively its content.
			FTPFile[] children = ftp.listFiles(existingParentDir + name);
			for (FTPFile child : children) {
				rm(existingParentDir + name, child.getName());
			}
			ftp.removeDirectory(existingParentDir + name);
		}

	}

	@Override
	public void rename(String existingParentDir, String oldName, String newName)
			throws Exception {
		if (!existingParentDir.endsWith("/")) {
			existingParentDir += "/";
		}
		ftp.rename(existingParentDir + oldName, existingParentDir + newName);
	}

	@Override
	public String getDefaultLocalDir() {
		return System.getProperty("user.home");
	}

	@Override
	public void setParent(IContainer parent) {
		this.ds = (DataSource) parent;

	}

}
