package org.ocpteam.ui.swt;

import org.eclipse.jface.action.Action;
import org.ocpteam.misc.LOG;
import org.ocpteam.misc.swt.QuickMessage;

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