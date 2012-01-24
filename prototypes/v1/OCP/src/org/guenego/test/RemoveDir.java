package org.guenego.test;

import java.io.File;

import org.guenego.misc.JLG;


public class RemoveDir {
	public static void main(String[] args) {

		JLG.debug_on();
		String path = "C:\\Documents and Settings\\jlouis\\ftp\\local\\qqq";

		while (true) {
			File f = new File(path);
			File p = f;
			int i = 0;
			while (f.exists()) {
				i++;
				//JLG.debug("i=" + i);
				//if (i % 10 == 0) {
					p = f;
				//}
				f = new File(f, "qqq");

			}
			JLG.debug("i=" + i);
			p.delete();
		}
	}
}
