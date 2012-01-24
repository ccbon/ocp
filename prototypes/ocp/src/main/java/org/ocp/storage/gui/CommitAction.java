package org.ocp.storage.gui;

import java.io.File;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.TableItem;

import org.ocp.misc.JLG;

public class CommitAction extends Action {

	private UserExplorerComposite composite;


	public CommitAction(UserExplorerComposite userExplorerComposite) {
		this.composite = userExplorerComposite;
		// TODO: change the accelerator to ctrl + left arrow
		setText("&Commit@F7");
		setToolTipText("Commit");
	}


	public void run() {
		JLG.debug("Commit");
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
}