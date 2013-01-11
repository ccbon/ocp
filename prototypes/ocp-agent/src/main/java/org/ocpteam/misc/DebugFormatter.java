package org.ocpteam.misc;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class DebugFormatter extends Formatter {

	@Override
	public String format(LogRecord record) {
		Throwable t = (Throwable) record.getParameters()[0];
		Thread thread = (Thread) record.getParameters()[1];
		StackTraceElement ste = t.getStackTrace()[2];
		String classname = ste.getClassName();
		int i = classname.indexOf("$");
		if (i != -1) {
			classname = classname.substring(0, classname.indexOf("$"));
		}

		String sPrefix = record.getLevel() + " [T=" + thread.getName() + "] ("
				+ classname + ".java:" + ste.getLineNumber() + ") : ";
		return sPrefix + record.getMessage() + JLG.NL;
	}

}
