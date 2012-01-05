package com.guenego.ocp.gui.console;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;

import com.guenego.misc.JLG;
import com.guenego.ocp.Agent;

public class SignOutAction extends Action {
	private AdminConsole window;

	public SignOutAction(AdminConsole adminConsole) {
		window = adminConsole;
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
		window.setUser(null);
	}
}