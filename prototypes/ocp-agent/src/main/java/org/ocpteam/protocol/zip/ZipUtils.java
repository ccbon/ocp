package org.ocpteam.protocol.zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.ocpteam.misc.JLG;

public class ZipUtils {

	public static void extract(String zipfile, String path, File file) throws Exception {
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

}
