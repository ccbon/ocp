package org.ocpteam.ui.swt;

import org.eclipse.jface.action.Action;
import org.ocpteam.misc.JLG;


public class RemoveKeyAction extends Action {

	private DataSourceWindow w;


	public RemoveKeyAction(DataSourceWindow w) {
		this.w = w;
		setText("&Set Key@CTRL+ALT+S");
		setToolTipText("Set Key");
	}


	public void run() {
		JLG.debug("Set Key");
		MapComposite mapComposite = (MapComposite) w.explorerComposite;
		mapComposite.remove();
	}
}