package org.ocpteam.ui.jlg.swt;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.ocpteam.misc.JLG;

public class SignInAction extends Action {
	private DataSourceWindow window;

	public SignInAction(DataSourceWindow w) {
		window = w;
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
		if (window.agent.authenticatesWithSSH()) {
			SSHSignInWizard.start(window);
		} else {
			SignInWizard.start(window);
		}
	}
}