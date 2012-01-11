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
		for (TableItem item : composite.localDirectoryTable.getSelection()) {
			composite.deleteLocalFile(item);
		}
	}
}