package com.guenego.ocp.gui.console;

import java.io.File;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.TableItem;

import com.guenego.misc.JLG;
import com.guenego.ocp.Agent;
import com.guenego.ocp.FileSystem;
import com.guenego.ocp.TreeEntry;
import com.guenego.ocp.User;

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
			User user = composite.user;
			Agent agent = composite.agent;
			File parentDir = composite.currentLocalDirectory;
			String name = item.getText(0);
			TreeEntry te = composite.currentTree.getEntry(name);
			FileSystem fs = new FileSystem(user, agent, null);
			try {
				fs.checkout(te, parentDir);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			composite.reloadLocalDirectoryTable();
		}
		
	}
}