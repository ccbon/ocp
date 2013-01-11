package org.ocpteamx.protocol.zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.ocpteam.misc.LOG;

public class ZipUtils {

	public static void extract(File zipfile, String path, File file)
			throws Exception {
		LOG.debug("extracting from " + zipfile + " path=" + path);
		ZipInputStream zipInputStream = null;
		ZipEntry zipEntry = null;
		byte[] buffer = new byte[2048];
		try {
			zipInputStream = new ZipInputStream(new FileInputStream(zipfile));
			while ((zipEntry = zipInputStream.getNextEntry()) != null) {
				LOG.debug("zip path=" + zipEntry.getName());
				if (zipEntry.getName().equalsIgnoreCase(path)) {
					LOG.debug("found it.");
					FileOutputStream fileoutputstream = new FileOutputStream(
							file);
					int n;

					while ((n = zipInputStream.read(buffer, 0, 2048)) > -1) {
						fileoutputstream.write(buffer, 0, n);
					}

					fileoutputstream.close();
					zipInputStream.closeEntry();
					break;

				}
			}
		} finally {
			if (zipInputStream != null) {
				zipInputStream.close();
			}
		}
	}

	public static void rm(File zipFile, String filename) throws Exception {
		if (filename.startsWith("/")) {
			filename = filename.substring(1);
		}
		if (filename.endsWith("/")) {
			filename = filename.substring(0, filename.length() - 1);
		}
		File tempFile = File.createTempFile(zipFile.getName(), null);

		tempFile.delete();
		tempFile.deleteOnExit();

		boolean renameOk = zipFile.renameTo(tempFile);
		if (!renameOk) {
			throw new RuntimeException("could not rename the file "
					+ zipFile.getAbsolutePath() + " to "
					+ tempFile.getAbsolutePath());
		}
		byte[] buf = new byte[1024];

		ZipInputStream zin = new ZipInputStream(new FileInputStream(tempFile));
		ZipOutputStream zout = new ZipOutputStream(
				new FileOutputStream(zipFile));

		ZipEntry entry = null;
		while ((entry = zin.getNextEntry()) != null) {
			String name = entry.getName();
			boolean toBeDeleted = false;
			if (name.startsWith(filename + "/") || name.equals(filename)) {
				toBeDeleted = true;
			}
			if (!toBeDeleted) {
				// Add ZIP entry to output stream.
				zout.putNextEntry(new ZipEntry(name));
				// Transfer bytes from the ZIP file to the output file
				int len;
				while ((len = zin.read(buf)) > 0) {
					zout.write(buf, 0, len);
				}
			}
		}
		// Close the streams
		zin.close();
		// Compress the files
		// Complete the ZIP file
		zout.close();
		tempFile.delete();

	}

	public static void rename(File zipFile, String oldname, String newname)
			throws Exception {
		if (oldname.startsWith("/")) {
			oldname = oldname.substring(1);
		}
		if (oldname.endsWith("/")) {
			oldname = oldname.substring(0, oldname.length() - 1);
		}
		if (newname.startsWith("/")) {
			newname = newname.substring(1);
		}
		File tempFile = File.createTempFile(zipFile.getName(), null);

		tempFile.delete();
		tempFile.deleteOnExit();

		boolean renameOk = zipFile.renameTo(tempFile);
		if (!renameOk) {
			throw new RuntimeException("could not rename the file "
					+ zipFile.getAbsolutePath() + " to "
					+ tempFile.getAbsolutePath());
		}
		byte[] buf = new byte[1024];

		ZipInputStream zin = new ZipInputStream(new FileInputStream(tempFile));
		ZipOutputStream zout = new ZipOutputStream(
				new FileOutputStream(zipFile));

		ZipEntry entry = null;
		while ((entry = zin.getNextEntry()) != null) {
			String name = entry.getName();
			if (name.startsWith("/")) {
				name = name.substring(1);
			}
			LOG.debug("name=" + name);
			LOG.debug("oldname=" + oldname);
			boolean toBeRenamed = false;
			if (name.startsWith(oldname + "/") || name.equals(oldname)) {
				LOG.debug("to be renamed.");
				toBeRenamed = true;
			}
			String newEntryName = name;
			if (toBeRenamed) {
				newEntryName = name.replaceFirst("\\Q" + oldname + "\\E",
						newname);
			}
			// Add ZIP entry to output stream.
			zout.putNextEntry(new ZipEntry(newEntryName));
			// Transfer bytes from the ZIP file to the output file
			int len;
			while ((len = zin.read(buf)) > 0) {
				zout.write(buf, 0, len);
			}

		}
		// Close the streams
		zin.close();
		// Compress the files
		// Complete the ZIP file
		zout.close();
		tempFile.delete();

	}

	public static void mkdir(File zipFile, String newDir) throws Exception {
		File tempFile = File.createTempFile(zipFile.getName(), null);

		tempFile.delete();
		tempFile.deleteOnExit();

		boolean renameOk = zipFile.renameTo(tempFile);
		if (!renameOk) {
			throw new RuntimeException("could not rename the file "
					+ zipFile.getAbsolutePath() + " to "
					+ tempFile.getAbsolutePath());
		}
		byte[] buf = new byte[1024];

		ZipInputStream zin = new ZipInputStream(new FileInputStream(tempFile));
		ZipOutputStream zout = new ZipOutputStream(
				new FileOutputStream(zipFile));

		ZipEntry entry = null;
		while ((entry = zin.getNextEntry()) != null) {
			String name = entry.getName();
			// Add ZIP entry to output stream.
			zout.putNextEntry(new ZipEntry(name));
			// Transfer bytes from the ZIP file to the output file
			int len;
			while ((len = zin.read(buf)) > 0) {
				zout.write(buf, 0, len);
			}

		}
		// add the empty directory.
		zout.putNextEntry(new ZipEntry(newDir + "/"));

		// Close the streams
		zin.close();
		// Compress the files
		// Complete the ZIP file
		zout.close();
		tempFile.delete();
	}

	public static void add(File zipFile, String dir, File parent, File[] files)
			throws Exception {

		if (dir.startsWith("/")) {
			dir = dir.substring(1);
		}
		if (dir.endsWith("/")) {
			dir = dir.substring(0, dir.length() - 1);
		}

		File tempFile = File.createTempFile(zipFile.getName(), null);
		try {
			tempFile.delete();
			tempFile.deleteOnExit();

			boolean renameOk = zipFile.renameTo(tempFile);
			if (!renameOk) {
				throw new RuntimeException("could not rename the file "
						+ zipFile.getAbsolutePath() + " to "
						+ tempFile.getAbsolutePath());
			}
			byte[] buf = new byte[1024];

			ZipInputStream zin = new ZipInputStream(new FileInputStream(
					tempFile));
			ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(
					zipFile));

			ZipEntry entry = null;
			while ((entry = zin.getNextEntry()) != null) {
				String name = entry.getName();
				if (name.startsWith("/")) {
					name = name.substring(1);
				}
				LOG.debug("name=" + name);

				boolean toBeReplaced = false;
				for (File f : files) {
					String base = parent.getCanonicalFile().toURI().getPath();
					String fname = f.getCanonicalFile().toURI().getPath()
							.replaceFirst("\\Q" + base + "\\E", "");
					LOG.debug("fname=" + fname);
					if (!dir.equals("")) {
						fname = dir + "/" + fname;
					}
					if (name.startsWith("/" + fname + "/")
							|| name.startsWith(fname + "/")
							|| name.equals("/" + fname) || name.equals(fname)) {
						LOG.debug("to be replaced.");
						toBeReplaced = true;
						break;
					}

				}
				if (!toBeReplaced) {
					// Add ZIP entry to output stream.
					zout.putNextEntry(new ZipEntry(name));
					// Transfer bytes from the ZIP file to the output file
					int len;
					while ((len = zin.read(buf)) > 0) {
						zout.write(buf, 0, len);
					}

				}
			}

			// add all the files
			LOG.debug("Adding the new files");
			for (File f : files) {
				String base = parent.getCanonicalFile().toURI().getPath();
				String fname = f.getCanonicalFile().toURI().getPath()
						.replaceFirst("\\Q" + base + "\\E", "");
				LOG.debug("fname=" + fname);
				if (!dir.equals("")) {
					fname = dir + "/" + fname;
				}
				if (f.isDirectory()) {
					if (!fname.endsWith("/")) {
						fname += "/";
					}
					zout.putNextEntry(new ZipEntry(fname));
				} else {
					InputStream in = new FileInputStream(f);
					// Add ZIP entry to output stream.
					zout.putNextEntry(new ZipEntry(fname));
					// Transfer bytes from the file to the ZIP file
					int len;
					while ((len = in.read(buf)) > 0) {
						zout.write(buf, 0, len);
					}
					in.close();
					// Complete the entry

				}

			}
			// Close the streams
			zin.close();
			// Compress the files
			// Complete the ZIP file
			zout.close();
		} catch (Exception e) {
			tempFile.renameTo(zipFile);
			throw e;
		}

	}

	public static void createEmptyFile(String filename) {
		try {
			// Create the ZIP file
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
					filename));
			// Complete the ZIP file
			out.close();
		} catch (Exception e) {
		}
	}
}
