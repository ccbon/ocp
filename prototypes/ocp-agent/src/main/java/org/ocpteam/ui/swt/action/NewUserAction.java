package org.ocpteam.ui.swt.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.ocpteam.misc.LOG;
import org.ocpteam.ui.swt.DataSourceWindow;
import org.ocpteam.ui.swt.wizard.NewUserWizard;


public class NewUserAction extends Action {
	private DataSourceWindow window;

	public NewUserAction(DataSourceWindow w) {
		window = w; 
		setText("&Create User");
		setToolTipText("Create a new user");
		try {
			ImageDescriptor i = ImageDescriptor
					.createFromImageData(new ImageData(DataSourceWindow.class
							.getResourceAsStream("new_user.png")));
			setImageDescriptor(i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		LOG.info("Creating a new User: display a wizard...");
		NewUserWizard.start(window);
	}
}