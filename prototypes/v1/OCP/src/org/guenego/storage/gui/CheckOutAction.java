package org.guenego.storage.gui;

import java.io.File;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.TableItem;
import org.guenego.misc.JLG;


public class CheckOutAction extends Action {

	private UserExplorerComposite composite;

	public CheckOutAction(UserExplorerComposite userExplorerComposite) {
		this.composite = userExplorerComposite;
		// TODO: change the accelerator to ctrl + right arrow
		setText("&Check Out@F6");
		setToolTipText("Check Out");
	}

	public void run() {
		JLG.debug("Check Out");
		for (TableItem item : composite.remoteDirectoryTable.getSelection()) {
			File localDir = composite.currentLocalDirectory;
			String name = item.getText(0);
			try {
				String remoteDir = composite.currentRemoteDirString;
				composite.agent.getFileSystem(composite.user).checkout(remoteDir, name, localDir);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			composite.reloadLocalDirectoryTable();
		}

	}
}