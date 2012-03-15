package org.ocpteam.ui.swt;

import org.eclipse.jface.action.Action;
import org.ocpteam.misc.JLG;


public class SetKeyAction extends Action {

	private DataSourceWindow w;


	public SetKeyAction(DataSourceWindow w) {
		this.w = w;
		setText("&Set Key@CTRL+ALT+S");
		setToolTipText("Set Key");
	}


	public void run() {
		JLG.debug("Set Key");
		SetKeyWizard wizard = new SetKeyWizard(w);
		wizard.start();
	}
}