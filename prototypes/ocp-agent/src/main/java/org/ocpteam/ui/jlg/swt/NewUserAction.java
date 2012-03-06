package org.ocpteam.ui.jlg.swt;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.ocpteam.misc.JLG;


public class NewUserAction extends Action {
	private DataSourceWindow window;

	public NewUserAction(DataSourceWindow w) {
		window = w; 
		setText("&Create User");
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
		NewUserWizard.start(window);
	}
}