package org.ocpteam.misc;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

public class LOG {
	public static Set<String> set = new HashSet<String>();
	public static boolean bUseSet = false;

	private static Logger logger = null;
	private static StreamHandler fh;
	private static FileHandler fileHandler;

	public static void debug(String input) {
		log(Level.INFO, input);
	}

	private static void log(Level level, String msg) {
		checkInit();
		LogRecord r = new LogRecord(level, msg);
		Object[] parameters = new Object[2];
		Throwable t = new Throwable();
		parameters[0] = t;
		parameters[1] = Thread.currentThread();
		r.setParameters(parameters);
		StackTraceElement ste = t.getStackTrace()[2];
		r.setSourceClassName(ste.getClassName());

		logger.log(r);
		fh.flush();
	}

	public static void checkInit() {
		if (logger == null) {
			init();
		}
	}

	public static void init() {
		fh = new StreamHandler(System.out, new DebugFormatter());
		logger = Logger.getLogger("yannis");
		logger.setUseParentHandlers(false);
		logger.addHandler(fh);
		logger.setFilter(new Filter() {

			@Override
			public boolean isLoggable(LogRecord record) {
				String classname = record.getSourceClassName();
				int i = classname.indexOf("$");
				if (i != -1) {
					classname = classname.substring(0, classname.indexOf("$"));
				}
				if (bUseSet && !set.contains(classname)) {
					return false;
				}
				return true;
			}
		});

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				if (fileHandler != null) {
					fileHandler.flush();
					fileHandler.close();
				}
			}
		});
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

	public static void logInFile(String logfile) throws Exception {
		if (JLG.isNullOrEmpty(logfile)) {
			logger.removeHandler(fileHandler);
		} else {
			JLG.mkdir(JLG.dirname(escape(logfile)));
			if (fileHandler == null) {
				fileHandler = new FileHandler(logfile);
				fileHandler.setFormatter(new DebugFormatter());
				logger.addHandler(fileHandler);
			}
		}
	}

	private static String escape(String logfile) {
		LOG.debug("logfile=" + logfile);
		return logfile.replaceAll("%t", System.getProperty("java.io.tmpdir"))
				.replaceAll("%h", System.getProperty("user.home"));
	}

}
