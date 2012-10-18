package org.ocpteam.win32;

import java.io.File;
import java.io.IOException;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.WString;

/** Simple example of JNA interface mapping and usage. */
public class WindowsKernel32 {
	interface Kernel32 extends Library {
		public int GetFileAttributesW(WString fileName);
	}

	static Kernel32 lib = null;

	public static int getWin32FileAttributes(File f) throws IOException {
		if (!Platform.isWindows()) {
			return 0;
		}
		if (lib == null) {
			synchronized (Kernel32.class) {
				lib = (Kernel32) Native.loadLibrary("kernel32", Kernel32.class);
			}
		}
		return lib.GetFileAttributesW(new WString(f.getCanonicalPath()));
	}

	public static boolean isJunctionOrSymlink(File f) throws IOException {
		return (f.exists() && (0x400 & getWin32FileAttributes(f)) != 0);
	}
}