package com.guenego.storage.gui.console;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;

import com.guenego.misc.JLG;
import com.guenego.storage.Agent;

public class NewUserAction extends Action {
	private Display display;
	private Agent agent;
	private AdminConsole window;

	public NewUserAction(Agent a, Display d, AdminConsole adminConsole) {
		agent = a;
		display = d;
		window = adminConsole; 
		setText("&Create User@Ctrl+N");
		setToolTipText("Create a new user");
		try {
			ImageDescriptor i = ImageDescriptor
					.createFromImageData(new ImageData(ExitAction.class
							.getResourceAsStream("new_user.png")));
			setImageDescriptor(i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		JLG.debug("Creating a new User: display a wizard...");
		NewUserWizard.start(display, agent, window);
	}
}