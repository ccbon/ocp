package com.guenego.ocp.gui.console;

import java.io.File;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.TableItem;

import com.guenego.misc.JLG;
import com.guenego.ocp.Agent;
import com.guenego.ocp.FileSystem;
import com.guenego.ocp.User;

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
			User user = composite.user;
			Agent agent = composite.agent;
			File file = new File(composite.currentLocalDirectory, name);
			
			FileSystem fs = new FileSystem(user, agent, null);
			try {
				fs.commitFile(composite.currentRemoteDirString, file);
				composite.synchronizeRemote();
				composite.reloadRemoteDirectoryTable();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			

		}
		
	}
}