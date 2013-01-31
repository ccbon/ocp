package org.ocpteam.ui.swt.action;

import java.io.File;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.TableItem;
import org.ocpteam.misc.LOG;
import org.ocpteam.misc.swt.QuickMessage;
import org.ocpteam.ui.swt.DataSourceWindow;
import org.ocpteam.ui.swt.Task;
import org.ocpteam.ui.swt.composite.ExplorerComposite;

public class CheckOutAction extends Action {

	private DataSourceWindow w;

	public CheckOutAction(DataSourceWindow w) {
		this.w = w;
		// TODO: change the accelerator to ctrl + right arrow
		setText("Check &Out@F6");
		setToolTipText("Check Out");
	}

	@Override
	public void run() {
		LOG.debug("Check Out");
		if (!(w.explorerComposite instanceof ExplorerComposite)) {
			return;
		}

		final ExplorerComposite composite = (ExplorerComposite) w.explorerComposite;
		if (composite == null) {
			return;
		}
		for (TableItem item : composite.remoteDirectoryTable.getSelection()) {
			final String name = item.getText(0);

			w.getMonitor().monitor(new Task(name, new Runnable() {
				@Override
				public void run() {
					File localDir = composite.currentLocalDirectory;
					try {
						String remoteDir = composite.currentRemoteDirString;
						composite.fs.checkout(remoteDir, name, localDir);
						w.getShell().getDisplay().asyncExec(new Runnable() {
							@Override
							public void run() {
								LOG.debug("Refresh");
								composite.reloadLocalDirectoryTable();
							}
						});
					} catch (Exception e) {
						e.printStackTrace();
						QuickMessage.exception(w.getShell(), "Checkout failed.", e);
					}
				}
			}));
		}

	}

	public boolean canRun() {
		if (!(w.explorerComposite instanceof ExplorerComposite)) {
			return false;
		}
		ExplorerComposite composite = (ExplorerComposite) w.explorerComposite;
		if (composite == null) {
			return false;
		}
		return composite.remoteDirectoryTable.getSelection().length > 0;
	}
}