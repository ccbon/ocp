package org.ocpteam.ui.swt;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.ocpteam.misc.JLG;


public class SelectAllAction extends Action {
	private DataSourceWindow window;
	

	public SelectAllAction(DataSourceWindow w) {
		window = w;
		setText("Select All@Ctrl+A");
		setToolTipText("Select All");
	}

	public void run() {
		JLG.debug("Select All");
		Control c = window.getShell().getDisplay().getFocusControl();
		if (c.getClass() == Table.class) {
			Table t = (Table) c;
			t.select(1, t.getItemCount() - 1);
		}
	}
}