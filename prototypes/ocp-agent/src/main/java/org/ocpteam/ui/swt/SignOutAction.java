package org.ocpteam.ui.swt;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.ocpteam.misc.LOG;

public class SignOutAction extends Action {
	private DataSourceWindow window;

	public SignOutAction(DataSourceWindow window) {
		this.window = window;
		setText("S&ign out@Ctrl+D");
		setToolTipText("Sign out");
		try {
			ImageDescriptor i = ImageDescriptor
					.createFromImageData(new ImageData(ExitAction.class
							.getResourceAsStream("sign_out.png")));
			setImageDescriptor(i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		LOG.debug("Disconnect a user...");
		try {
			window.signOut();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			window.refresh();
		}
	}
}