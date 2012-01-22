package com.guenego.storage.gui.console;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;

import com.guenego.misc.JLG;

public class SelectAllAction extends Action {
	private AdminConsole window;
	

	public SelectAllAction(AdminConsole w) {
		window = w;
		setText("Select All@Ctrl+A");
		setToolTipText("Select All");
	}

	public void run() {
		JLG.debug("Select All");
		Control c = window.getShell().getDisplay().getFocusControl();
		if (c.getClass() == Table.class) {
			((Table) c).selectAll();
		}
	}
}