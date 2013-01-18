package org.ocpteam.ui.swt.action;

import org.eclipse.jface.action.Action;
import org.ocpteam.misc.LOG;
import org.ocpteam.misc.swt.QuickMessage;
import org.ocpteam.ui.swt.DataSourceWindow;
import org.ocpteam.ui.swt.MapComposite;

public class RemoveKeyAction extends Action {

	private DataSourceWindow w;

	public RemoveKeyAction(DataSourceWindow w) {
		this.w = w;
		setText("&Remove Key@DEL");
		setToolTipText("Remove Key");
	}

	@Override
	public void run() {
		LOG.debug("Remove Key");
		try {
			MapComposite mapComposite = (MapComposite) w.explorerComposite;
			mapComposite.remove();
		} catch (Exception e) {
			e.printStackTrace();
			QuickMessage.error(w.getShell(), "cannot remove");
		}
	}
}