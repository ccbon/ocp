package org.ocpteam.ui.swt.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.ocpteam.misc.LOG;
import org.ocpteam.ui.swt.DataSourceWindow;

public class SignOutAction extends Action {
	private DataSourceWindow window;

	public SignOutAction(DataSourceWindow window) {
		this.window = window;
		setText("S&ign out@Ctrl+D");
		setToolTipText("Sign out");
		try {
			ImageDescriptor i = ImageDescriptor
					.createFromImageData(new ImageData(DataSourceWindow.class
							.getResourceAsStream("sign_out.png")));
			setImageDescriptor(i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		LOG.info("Disconnect a user...");
		try {
			window.signOut();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			window.refresh();
		}
	}
}