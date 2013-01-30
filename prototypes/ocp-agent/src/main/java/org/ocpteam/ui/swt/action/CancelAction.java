package org.ocpteam.ui.swt.action;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.TableItem;
import org.ocpteam.misc.swt.QuickMessage;
import org.ocpteam.ui.swt.DataSourceWindow;
import org.ocpteam.ui.swt.Task;
import org.ocpteam.ui.swt.composite.ExplorerComposite;

public class CancelAction extends Action {
	private DataSourceWindow w;

	public CancelAction(DataSourceWindow w) {
		this.w = w;
		setText("Cancel");
		setToolTipText("Cancel task");
	}

	@Override
	public void run() {
		ExplorerComposite explorerComposite = null;
		CTabFolder tabFolder = w.tabFolder;
		CTabItem[] items = tabFolder.getItems();
		for (CTabItem item : items) {
			if (item.getControl().getClass() == ExplorerComposite.class) {
				explorerComposite = (ExplorerComposite) item.getControl();
			}
		}
		if (explorerComposite == null) {
			QuickMessage.error(w.getShell(),
					"Cant find the explorer composite");
			return;
		}
		for (TableItem item : explorerComposite.table.getSelection()) {
			Task task = (Task) item.getData();
			w.getMonitor().map.get(task).future.cancel(true);
			w.getMonitor().showProgress(task);
		}
	}
}
