package org.ocpteam.ui.swt;

import java.io.File;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.TableItem;
import org.ocpteam.misc.JLG;


public class CommitAction extends Action {

	private AdminConsole w;


	public CommitAction(AdminConsole w) {
		this.w = w;
		// TODO: change the accelerator to ctrl + left arrow
		setText("&Commit@F7");
		setToolTipText("Commit");
	}


	public void run() {
		JLG.debug("Commit");
		ExplorerComposite composite = w.explorerComposite;
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
		ExplorerComposite composite = w.explorerComposite;
		if (composite == null) {
			return false;
		}
		return composite.localDirectoryTable.getSelection().length > 0;
	}
}