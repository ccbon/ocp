package org.ocpteam.protocol.zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.ocpteam.misc.JLG;

public class ZipUtils {

	public static void extract(String zipfile, String path, File file)
			throws Exception {
		JLG.debug("extracting from " + zipfile + " path=" + path);
		ZipInputStream zipInputStream = null;
		ZipEntry zipEntry = null;
		byte[] buffer = new byte[2048];
		try {
			zipInputStream = new ZipInputStream(new FileInputStream(zipfile));
			while ((zipEntry = zipInputStream.getNextEntry()) != null) {
				JLG.debug("zip path=" + zipEntry.getName());
				if (zipEntry.getName().equalsIgnoreCase(path)) {
					JLG.debug("found it.");
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
		if (filename.endsWith("/")) {
			filename = filename.substring(0, filename.length() - 1);
		}
		File tempFile = File.createTempFile(zipFile.getName(), null);

		tempFile.delete();
		tempFile.deleteOnExit();

		boolean renameOk = zipFile.renameTo(tempFile);
		if (!renameOk) {
			throw new RuntimeException("could not rename the file " + zipFile.getAbsolutePath()
					+ " to " + tempFile.getAbsolutePath());
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

}
