package org.ocpteam.misc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.security.MessageDigest;

public class TestUtils {

	public static void createBigFile(String filename) throws Exception {
		JLG.rm(filename);
		FileWriter fw = new FileWriter(filename);
		for (int i = 0; i < 1000000; i++) {
			fw.write("truc bidule a ecrire\n");
			fw.flush();
		}
		fw.close();	
	}
	
	public static Id checksum(String filename) throws Exception {
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		md.reset();
		FileInputStream fis = new FileInputStream(new File(filename));
		byte[] input = new byte[1024];
		while (fis.read(input) < 0) {
			md.update(input);
		}
		fis.close();
		return new Id(md.digest());
	}

}
