package org.ocpteam.ui.swt.action;

import org.eclipse.jface.action.Action;
import org.ocpteam.misc.LOG;
import org.ocpteam.ui.swt.DataSourceWindow;
import org.ocpteam.ui.swt.NetworkPropertiesDialog;

public class ViewNetworkPropertiesAction extends Action {

	private DataSourceWindow window;

	public ViewNetworkPropertiesAction(DataSourceWindow window) {
		this.window = window;
		setText("&Network properties");
		setToolTipText("View network properties");
	}

	@Override
	public void run() {
		LOG.info("View Network properties...");
		NetworkPropertiesDialog dialog = new NetworkPropertiesDialog(window.getShell(), window);
		dialog.open();
		window.getShell().setFocus();
	}
}
