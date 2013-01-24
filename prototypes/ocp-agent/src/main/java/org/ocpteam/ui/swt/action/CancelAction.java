package org.ocpteam.ui.swt.action;

import org.eclipse.jface.action.Action;
import org.ocpteam.misc.LOG;
import org.ocpteam.ui.swt.DataSourceWindow;

public class CancelAction extends Action {
	private DataSourceWindow w;

	public CancelAction(DataSourceWindow w) {
		this.w = w;
		setText("Cancel");
		setToolTipText("Cancel selected transfert.");
	}

	@Override
	public void run() {
		LOG.debug("Cancel...");
	}
}
