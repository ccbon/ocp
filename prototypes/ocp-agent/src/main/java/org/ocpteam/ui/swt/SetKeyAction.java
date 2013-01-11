package org.ocpteam.ui.swt;

import org.eclipse.jface.action.Action;
import org.ocpteam.misc.LOG;


public class SetKeyAction extends Action {

	private DataSourceWindow w;


	public SetKeyAction(DataSourceWindow w) {
		this.w = w;
		setText("&Set Key@Insert");
		setToolTipText("Set Key");
	}


	@Override
	public void run() {
		LOG.debug("Set Key");
		SetKeyWizard wizard = new SetKeyWizard(w);
		wizard.start();
	}
}