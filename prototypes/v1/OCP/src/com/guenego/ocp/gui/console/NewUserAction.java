package com.guenego.ocp.gui.console;

import java.net.URL;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;

import com.guenego.misc.JLG;
import com.guenego.ocp.gui.install.ConfigWizard;

public class NewUserAction extends Action {
	AdminConsole window;
	private Display display;

	public NewUserAction(AdminConsole w, Display d) {
		window = w;
		display = d;
		setText("&Create User@Ctrl+N");
		setToolTipText("Create a new user");
		try {
			ImageDescriptor i = ImageDescriptor.createFromURL(new URL(
					"file:images/new_user.png"));
			setImageDescriptor(i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		JLG.debug("Creating a new User: display a wizard...");
		NewUserWizard.start(display);
	}
}