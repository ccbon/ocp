package com.guenego.ocp.gui.console;

import java.net.URL;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;

import com.guenego.misc.JLG;
import com.guenego.ocp.Agent;

public class SignInAction extends Action {
	private Display display;
	private Agent agent;

	public SignInAction(Agent a, Display d) {
		agent = a;
		display = d;
		setText("&Sign in@Ctrl+L");
		setToolTipText("User Authentication");
		try {
			ImageDescriptor i = ImageDescriptor.createFromURL(new URL(
					"file:images/sign_in.png"));
			setImageDescriptor(i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		JLG.debug("Authentication User: display a wizard...");
		SignInWizard.start(display, agent);
	}
}