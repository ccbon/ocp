package org.ocpteam.misc;

public class Application {

	public static final String TESTDIR = System.getProperty("java.io.tmpdir") + "/test";
	private static String appdir;

	public static String getAppDir() throws Exception {
		if (appdir == null) {
			throw new Exception(
					"Application directory must be created at the application start.");
		}
		return appdir;
	}

	public static void setAppDir(String appdir) {
		Application.appdir = appdir;
	}

}
