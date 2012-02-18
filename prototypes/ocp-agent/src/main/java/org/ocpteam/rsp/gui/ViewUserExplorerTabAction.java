package org.ocpteam.rsp.gui;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.ocpteam.misc.JLG;


public class ViewUserExplorerTabAction extends Action {
	private AdminConsole window;

	public ViewUserExplorerTabAction(AdminConsole adminConsole) {
		window = adminConsole;
		setText("User Explorer@Ctrl+E");
		setToolTipText("View User Explorer Tab");
		try {
			ImageDescriptor i = ImageDescriptor.createFromImageData(new ImageData(ViewUserExplorerTabAction.class.getResourceAsStream("view_user_explorer.png")));
			setImageDescriptor(i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		JLG.debug("View User Explorer.");
		window.addUserExplorerTab();
	}
}