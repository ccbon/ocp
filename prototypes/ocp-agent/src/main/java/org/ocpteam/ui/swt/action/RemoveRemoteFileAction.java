package org.ocpteam.ui.swt.action;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.TableItem;
import org.ocpteam.misc.LOG;
import org.ocpteam.misc.swt.QuickMessage;
import org.ocpteam.ui.swt.ExplorerComposite;


public class RemoveRemoteFileAction extends Action {

	private ExplorerComposite composite;


	public RemoveRemoteFileAction(ExplorerComposite explorerComposite) {
		this.composite = explorerComposite;
		setText("&Delete@DEL");
		setToolTipText("Delete remote file");
	}


	@Override
	public void run() {
		LOG.debug("Delete remote file");
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