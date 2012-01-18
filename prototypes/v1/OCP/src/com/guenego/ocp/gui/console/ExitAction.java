package com.guenego.ocp.gui.console;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import com.guenego.misc.JLG;
import com.guenego.storage.Agent;

public class ExitAction extends Action {
	private Agent agent;
	private Display display;
	private AdminConsole window;
	

	public ExitAction(Agent a, Display d, AdminConsole w) {
		agent = a;
		display = d;
		window = w;
		setText("E&xit@Ctrl+W");
		setToolTipText("Exit the application");
		try {
			ImageDescriptor i = ImageDescriptor
					.createFromImageData(new ImageData(ExitAction.class
							.getResourceAsStream("exit.png")));
			setImageDescriptor(i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		JLG.debug("Exiting...");
		MessageBox messageBox = new MessageBox(new Shell(display),
				SWT.ICON_WARNING | SWT.YES | SWT.NO);
		messageBox
				.setMessage("This will stop the OCP agent. Are you sure you want to exit ?");
		messageBox.setText("Warning");
		int buttonID = messageBox.open();
		switch (buttonID) {
		case SWT.YES:
			display.dispose();
			agent.stop();
			break;
		case SWT.NO:
			Shell shell = window.getShell();
			if (shell != null) {
				shell.setFocus();
			}
			break;
		}
	}
}