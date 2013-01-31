package org.ocpteam.ui.swt.action;

import org.eclipse.jface.action.Action;
import org.ocpteam.misc.LOG;
import org.ocpteam.ui.swt.DataSourceWindow;
import org.ocpteam.ui.swt.wizard.SetKeyWizard;


public class SetKeyAction extends Action {

	private DataSourceWindow w;


	public SetKeyAction(DataSourceWindow w) {
		this.w = w;
		setText("&Set Key@Insert");
		setToolTipText("Set Key");
	}


	@Override
	public void run() {
		LOG.info("Set Key");
		SetKeyWizard wizard = new SetKeyWizard(w);
		wizard.start();
	}
}