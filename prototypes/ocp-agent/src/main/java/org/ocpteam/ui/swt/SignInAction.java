package org.ocpteam.ui.swt;

import java.util.ResourceBundle;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.ocpteam.layer.rsp.DataSource;
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
		Scenario scenario = null;
		try {
			ResourceBundle swt = DataSource.getResource(
					window.ds.getProtocol(), "swt");
			scenario = (Scenario) swt.getObject("SignInScenario");
		} catch (Exception e) {
		}
		try {
			if (scenario != null) {
				scenario.run(window);
				window.signIn();
			} else {
				SignInWizard.start(window);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}