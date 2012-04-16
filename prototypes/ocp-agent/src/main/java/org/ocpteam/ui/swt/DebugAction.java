package org.ocpteam.ui.swt;

import org.eclipse.jface.action.Action;
import org.ocpteam.misc.JLG;


public class DebugAction extends Action {

	public DebugAction() {
		setText("&Debug Mode");
		setToolTipText("Debug Mode");
		setChecked(JLG.getDebugStatus());
	}

	@Override
	public void run() {

		if (isChecked()) {
			JLG.debug("Debug switch on");
			JLG.debug_on();
		} else {
			JLG.debug("Debug switch off");
			JLG.debug_off();
		}
	}
}