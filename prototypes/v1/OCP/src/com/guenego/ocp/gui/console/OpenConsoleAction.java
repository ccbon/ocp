package com.guenego.ocp.gui.console;

import java.net.URL;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;

import com.guenego.misc.JLG;

public class OpenConsoleAction extends Action {

	private Display display;
	private AdminConsole window;

	public OpenConsoleAction(AdminConsole w, Display d) {
		window = w;
		display = d;
		setText("&Open Console@Ctrl+O");
		setToolTipText("Open the OCP Agent Administration Console");
		try {
			ImageDescriptor i = ImageDescriptor.createFromURL(new URL(
					"file:images/console.png"));
			setImageDescriptor(i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		JLG.debug("Opening Console");
		window.open();
	}



}
