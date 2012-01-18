package com.guenego.ocp.gui.console;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;

import com.guenego.misc.JLG;
import com.guenego.storage.Agent;

public class SignInAction extends Action {
	private Display display;
	private Agent agent;
	private AdminConsole window;

	public SignInAction(Agent a, Display d, AdminConsole adminConsole) {
		agent = a;
		display = d;
		window = adminConsole;
		setText("&Sign in@Ctrl+L");
		setToolTipText("User Authentication");
		try {
			ImageDescriptor i = ImageDescriptor
					.createFromImageData(new ImageData(ExitAction.class
							.getResourceAsStream("sign_in.png")));
			setImageDescriptor(i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		JLG.debug("Authentication User: display a wizard...");
		SignInWizard.start(display, agent, window);
	}
}