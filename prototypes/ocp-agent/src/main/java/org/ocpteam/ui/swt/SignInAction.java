package org.ocpteam.ui.swt;

import java.util.ResourceBundle;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.ocpteam.component.Authentication;
import org.ocpteam.component.UserIdentification;
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

	@Override
	public void run() {
		JLG.debug("Authentication User: display a wizard...");
		Scenario scenario = null;
		try {
			ResourceBundle swt = window.ds.getResource("swt");
			scenario = (Scenario) swt.getObject("SignInScenario");
		} catch (Exception e) {
		}
		try {
			if (scenario != null) {
				scenario.setWindow(window);
				scenario.run();
				window.signIn();
			} else {
				if (window.ds.usesComponent(Authentication.class)) {
					SignInWizard.start(window);
				}
				if (window.ds.usesComponent(UserIdentification.class)) {
					SignInUIWizard.start(window);
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}