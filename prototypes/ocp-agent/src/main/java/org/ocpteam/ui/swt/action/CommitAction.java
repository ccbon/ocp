package org.ocpteam.ui.swt.action;

import java.io.File;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.TableItem;
import org.ocpteam.misc.LOG;
import org.ocpteam.ui.swt.DataSourceWindow;
import org.ocpteam.ui.swt.composite.ExplorerComposite;


public class CommitAction extends Action {

	private DataSourceWindow w;


	public CommitAction(DataSourceWindow w) {
		this.w = w;
		// TODO: change the accelerator to ctrl + left arrow
		setText("Co&mmit@F7");
		setToolTipText("Commit");
	}


	@Override
	public void run() {
		LOG.debug("Commit");
		if (!(w.explorerComposite instanceof ExplorerComposite)) {
			return;			
		}

		ExplorerComposite composite = (ExplorerComposite) w.explorerComposite;

		if (composite == null) {
			return;
		}
		for (TableItem item : composite.localDirectoryTable.getSelection()) {
			String name = item.getText(0);
			File file = new File(composite.currentLocalDirectory, name);
			try {
				composite.fs.commit(composite.currentRemoteDirString, file);
				composite.reloadRemoteDirectoryTable();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			

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
		return composite.localDirectoryTable.getSelection().length > 0;
	}
}