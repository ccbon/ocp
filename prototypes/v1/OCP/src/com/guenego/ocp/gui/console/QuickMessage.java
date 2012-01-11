package com.guenego.ocp.gui.console;

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

}
