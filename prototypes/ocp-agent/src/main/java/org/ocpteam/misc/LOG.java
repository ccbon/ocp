package org.ocpteam.misc;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

public class LOG {

	private static Logger logger = null;

	public static void debug(String input) {
		log(Level.INFO, input);
	}

	private static void log(Level level, String msg) {
		checkInit();
		LogRecord r = new LogRecord(level, msg);
		Object[] parameters = new Object[2];
		parameters[0] = new Throwable();
		parameters[1] = Thread.currentThread();
		r.setParameters(parameters);
		logger.log(r);
	}

	private static void checkInit() {
		if (logger == null) {
			init();
		}
	}

	public static void init() {
		Handler fh = new StreamHandler(System.out, new DebugFormatter());
		logger = Logger.getLogger("yannis");
		logger.setUseParentHandlers(false);
		logger.addHandler(fh);
	}

	public static void debug_on() {
		checkInit();
		logger.setLevel(Level.INFO);
		debug("debug on");
	}

	public static void debug_off() {
		checkInit();
		logger.setLevel(Level.WARNING);
	}

	public static boolean getDebugStatus() {
		return logger.getLevel().intValue() <= Level.INFO.intValue();
	}

	public static void debugStackTrace() {
		Throwable t = new Throwable();
		StringWriter result = new StringWriter();
		t.printStackTrace(new PrintWriter(result));
		debug(result.toString());
	}

	public static void error(Exception e) {
		System.out.println("ERROR: " + e.getMessage());
		e.printStackTrace();
	}

	public static void error(String string) {
		System.out.println("ERROR: " + string);

	}

	public static void warn(Exception e) {
		System.out.println("WARNING: ");
		e.printStackTrace(System.out);

	}

	public static void warn(String string) {
		System.out.println("WARNING: " + string);

	}

}
