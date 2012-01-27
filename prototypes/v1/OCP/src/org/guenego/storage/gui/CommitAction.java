package org.guenego.storage.gui;

import java.io.File;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.TableItem;
import org.guenego.misc.JLG;


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
		UserExplorerComposite composite = w.userExplorerComposite;
		if (composite == null) {
			return;
		}
		for (TableItem item : composite.localDirectoryTable.getSelection()) {
			String name = item.getText(0);
			File file = new File(composite.currentLocalDirectory, name);
			try {
				composite.agent.getFileSystem(composite.user).commit(composite.currentRemoteDirString, file);
				composite.reloadRemoteDirectoryTable();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			

		}
		
	}


	public boolean canRun() {
		UserExplorerComposite composite = w.userExplorerComposite;
		if (composite == null) {
			return false;
		}
		return composite.localDirectoryTable.getSelection().length > 0;
	}
}