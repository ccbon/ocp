package com.guenego.ocp.gui.console;

import java.net.URL;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;

import com.guenego.misc.JLG;
import com.guenego.ocp.gui.AdminConsole;

public class ExitAction extends Action {
	AdminConsole window;
	private Display display;

	public ExitAction(AdminConsole w, Display d) {
		window = w;
		display = d;
		setText("E&xit@Ctrl+W");
		setToolTipText("Exit the application");
		try {
			ImageDescriptor i = ImageDescriptor.createFromURL(new URL(
					"file:images/exit.png"));
			setImageDescriptor(i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		JLG.debug("Exiting...");
		window.agent.stop();
		display.dispose();
		System.exit(0);
	}
}