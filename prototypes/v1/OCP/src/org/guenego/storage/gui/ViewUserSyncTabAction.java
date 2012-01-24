package org.guenego.storage.gui;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.guenego.misc.JLG;


public class ViewUserSyncTabAction extends Action {
	private AdminConsole window;

	public ViewUserSyncTabAction(AdminConsole adminConsole) {
		window = adminConsole;
		setText("User Synchronize@Ctrl+H");
		setToolTipText("View User Synchronize Tab");
		try {
			ImageDescriptor i = ImageDescriptor.createFromImageData(new ImageData(ViewUserSyncTabAction.class.getResourceAsStream("view_user_sync.png")));
			setImageDescriptor(i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		JLG.debug("View User Synchronization.");
		window.addUserSyncTab();
	}
}