package org.ocpteam.storage.gui;

import org.eclipse.jface.action.Action;
import org.ocpteam.misc.JLG;


public class CreateNewRemoteDirAction extends Action {

	private UserExplorerComposite composite;


	public CreateNewRemoteDirAction(UserExplorerComposite userExplorerComposite) {
		this.composite = userExplorerComposite;
		setText("&New folder@Ctrl+N");
		setToolTipText("New folder");
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
		JLG.debug("Create New Directory");
		composite.createNewRemoteDir();
	}
}