package org.ocpteam.ui.swt.action;

import java.util.ResourceBundle;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.ocpteam.interfaces.IAuthenticable;
import org.ocpteam.misc.LOG;
import org.ocpteam.misc.swt.QuickMessage;
import org.ocpteam.ui.swt.DataSourceWindow;
import org.ocpteam.ui.swt.IScenario;
import org.ocpteam.ui.swt.wizard.SignInWithAuthenticationWizard;
import org.ocpteam.ui.swt.wizard.SignInWizard;

public class SignInAction extends Action {
	private DataSourceWindow window;

	public SignInAction(DataSourceWindow w) {
		window = w;
		setText("&Sign in@Ctrl+L");
		setToolTipText("Sign in");
		try {
			ImageDescriptor i = ImageDescriptor
					.createFromImageData(new ImageData(DataSourceWindow.class
							.getResourceAsStream("sign_in.png")));
			setImageDescriptor(i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		LOG.info("Authentication User: display a wizard...");
		IScenario scenario = null;
		try {
			ResourceBundle swt = window.ds.getResource("swt");
			scenario = (IScenario) swt.getObject("SignInScenario");
		} catch (Exception e) {
		}
		try {
			if (scenario != null) {
				scenario.setWindow(window);
				scenario.run();
				if (scenario.succeeded()) {
					window.signIn();
				}
			} else {

				if (window.ds.usesComponent(IAuthenticable.class)) {
					SignInWithAuthenticationWizard.start(window);
				} else {
					SignInWizard.start(window);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			QuickMessage.error(window.getShell(), "Cannot connect.");
		}

	}
}