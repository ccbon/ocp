package org.ocpteam.ui.swt;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.ocpteam.misc.JLG;

public class SignOutAction extends Action {
	private DataSourceWindow window;

	public SignOutAction(DataSourceWindow window) {
		this.window = window;
		setText("S&ign out@Ctrl+D");
		setToolTipText("Disconnect a user");
		try {
			ImageDescriptor i = ImageDescriptor
					.createFromImageData(new ImageData(ExitAction.class
							.getResourceAsStream("sign_out.png")));
			setImageDescriptor(i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		JLG.debug("Disconnect a user...");
		try {
			window.signOut();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			window.refresh();
		}
	}
}