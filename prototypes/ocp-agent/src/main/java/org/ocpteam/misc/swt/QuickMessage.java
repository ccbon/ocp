package org.ocpteam.misc.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class QuickMessage {

	public static void error(Shell shell, String string) {
		MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
		messageBox.setMessage(string);
		messageBox.setText("Error");
		messageBox.open();
	}

	public static boolean confirm(Shell shell, String string) {
		MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING
				| SWT.YES | SWT.NO);
		messageBox.setMessage(string);
		messageBox.setText("Warning");
		if (messageBox.open() == SWT.YES) {
			return true;
		}
		return false;
	}

	public static void inform(Shell shell, String string) {
		MessageBox messageBox = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
		messageBox.setMessage(string);
		messageBox.setText("Information");
		messageBox.open();
	}

	public static Exception exception(Shell shell, String message, Exception e) {
		e.printStackTrace();
		error(shell, message + " - error: " + e.getMessage());
		return e;
	}

	public static Exception exception(Shell shell, String message) {
		error(shell, message);
		return new Exception(message);
	}

}
