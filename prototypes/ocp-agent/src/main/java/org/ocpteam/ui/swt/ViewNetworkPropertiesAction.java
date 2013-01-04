package org.ocpteam.ui.swt;

import org.eclipse.jface.action.Action;
import org.ocpteam.misc.JLG;

public class ViewNetworkPropertiesAction extends Action {

	private DataSourceWindow window;

	public ViewNetworkPropertiesAction(DataSourceWindow window) {
		this.window = window;
		setText("&Network properties");
		setToolTipText("View network properties");
	}

	@Override
	public void run() {
		JLG.debug("View Network properties...");
		NetworkPropertiesDialog dialog = new NetworkPropertiesDialog(window.getShell(), window);
		dialog.open();
		window.getShell().setFocus();
	}
}
