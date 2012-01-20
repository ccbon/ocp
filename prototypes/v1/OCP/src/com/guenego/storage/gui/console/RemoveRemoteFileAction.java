package com.guenego.storage.gui.console;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.TableItem;

import com.guenego.misc.JLG;

public class RemoveRemoteFileAction extends Action {

	private UserExplorerComposite composite;


	public RemoveRemoteFileAction(UserExplorerComposite userExplorerComposite) {
		this.composite = userExplorerComposite;
		setText("&Delete@DEL");
		setToolTipText("Delete remote file");
	}


	public void run() {
		JLG.debug("Delete remote file");
		int selNbr = composite.remoteDirectoryTable.getSelection().length;
		if (selNbr == 1) {
			String name = composite.remoteDirectoryTable.getSelection()[0].getText();
			if (!QuickMessage.confirm(composite.getShell(),
					"Are you sure you want to delete the file " + name + " ?")) {
				return;
			}
		} else {
			if (!QuickMessage.confirm(composite.getShell(),
					"Are you sure you want to delete these " + selNbr + " files?")) {
				return;
			}			
		}
		for (TableItem item : composite.remoteDirectoryTable.getSelection()) {
			composite.deleteRemoteFile(item);
		}
		composite.reloadRemoteDirectoryTable();
	}
}