package com.guenego.ocp.gui.console;

import org.eclipse.jface.action.Action;

import com.guenego.misc.JLG;

public class DebugAction extends Action {

	public DebugAction() {
		setText("&Debug Mode");
		setToolTipText("Debug Mode");
		setChecked(JLG.getDebugStatus());
	}

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