package com.guenego.ocp.gui.console;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.TableItem;

import com.guenego.misc.JLG;

public class RemoveRemoteFileAction extends Action {

	private UserExplorerComposite composite;


	public RemoveRemoteFileAction(UserExplorerComposite userExplorerComposite) {
		this.composite = userExplorerComposite;
		setText("&Delete@DEL");
		setToolTipText("Delete remote file");
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
		JLG.debug("Delete remote file");
		if (!QuickMessage.confirm(composite.getShell(), "Are you sure you want to delete selected files ?")) {
			return;
		}
		for (TableItem item : composite.remoteDirectoryTable.getSelection()) {
			composite.deleteRemoteFile(item);
		}
	}
}