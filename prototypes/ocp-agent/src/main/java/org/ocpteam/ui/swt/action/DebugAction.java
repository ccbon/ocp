package org.ocpteam.ui.swt.action;

import org.eclipse.jface.action.Action;
import org.ocpteam.misc.LOG;


public class DebugAction extends Action {

	public DebugAction() {
		setText("&Debug Mode");
		setToolTipText("Debug Mode");
		setChecked(LOG.getDebugStatus());
	}

	@Override
	public void run() {

		if (isChecked()) {
			LOG.info("Debug switch on");
			LOG.debug_on();
		} else {
			LOG.info("Debug switch off");
			LOG.debug_off();
		}
	}
}