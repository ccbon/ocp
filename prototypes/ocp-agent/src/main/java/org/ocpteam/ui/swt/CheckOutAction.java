package org.ocpteam.ui.swt;

import java.io.File;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.TableItem;
import org.ocpteam.misc.JLG;


public class CheckOutAction extends Action {

	private DataSourceWindow w;

	public CheckOutAction(DataSourceWindow w) {
		this.w = w;
		// TODO: change the accelerator to ctrl + right arrow
		setText("&Check Out@F6");
		setToolTipText("Check Out");
	}

	@Override
	public void run() {
		JLG.debug("Check Out");
		if (!(w.explorerComposite instanceof ExplorerComposite)) {
			return;			
		}

		ExplorerComposite composite = (ExplorerComposite) w.explorerComposite;
		if (composite == null) {
			return;
		}
		for (TableItem item : composite.remoteDirectoryTable.getSelection()) {
			File localDir = composite.currentLocalDirectory;
			String name = item.getText(0);
			try {
				String remoteDir = composite.currentRemoteDirString;
				composite.fs.checkout(remoteDir, name, localDir);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			composite.reloadLocalDirectoryTable();
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