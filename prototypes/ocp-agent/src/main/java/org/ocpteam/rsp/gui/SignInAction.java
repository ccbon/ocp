package org.ocpteam.rsp.gui;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.ocpteam.misc.JLG;
import org.ocpteam.sftp.gui.SSHSignInWizard;

public class SignInAction extends Action {
	private AdminConsole window;

	public SignInAction(AdminConsole adminConsole) {
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
		if (window.agent.connectsWithSSH()) {
			SSHSignInWizard.start(window);
		} else {
			SignInWizard.start(window);
		}
	}
}