package org.ocpteam.ui.swt;

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
			LOG.debug("Debug switch on");
			LOG.debug_on();
		} else {
			LOG.debug("Debug switch off");
			LOG.debug_off();
		}
	}
}