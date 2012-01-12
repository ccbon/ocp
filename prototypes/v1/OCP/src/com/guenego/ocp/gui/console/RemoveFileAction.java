package com.guenego.ocp.gui.console;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.TableItem;

import com.guenego.misc.JLG;

public class RemoveFileAction extends Action {

	private UserExplorerComposite composite;


	public RemoveFileAction(UserExplorerComposite userExplorerComposite) {
		this.composite = userExplorerComposite;
		setText("&Delete@DEL");
		setToolTipText("Delete");
//		try {
//			ImageDescriptor i = ImageDescriptor
//					.createFromImageData(new ImageData(OpenFileAction.class
//							.getResourceAsStream("about.png")));
//			setImageDescriptor(i);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}


	public void run() {
		JLG.debug("Delete local file");
		int selNbr = composite.localDirectoryTable.getSelection().length;
		if (selNbr == 1) {
			String name = composite.localDirectoryTable.getSelection()[0].getText();
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

		for (TableItem item : composite.localDirectoryTable.getSelection()) {
			composite.deleteLocalFile(item);
		}
		composite.reloadLocalDirectoryTable();
	}
}