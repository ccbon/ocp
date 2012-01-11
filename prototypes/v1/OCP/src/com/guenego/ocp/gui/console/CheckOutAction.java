package com.guenego.ocp.gui.console;

import java.io.File;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.TableItem;

import com.guenego.misc.JLG;
import com.guenego.ocp.TreeEntry;

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
			File parentDir = composite.currentLocalDirectory;
			String name = item.getText(0);
			try {
				TreeEntry te = composite.getCurrentTree().getEntry(name);
				composite.fs.checkout(te, parentDir);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			composite.reloadLocalDirectoryTable();
		}

	}
}