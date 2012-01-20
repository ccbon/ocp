package com.guenego.storage.gui.console;

import org.eclipse.jface.action.Action;

import com.guenego.misc.JLG;

public class CreateNewDirAction extends Action {

	private UserExplorerComposite composite;


	public CreateNewDirAction(UserExplorerComposite userExplorerComposite) {
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
		composite.createNewLocalDir();
	}
}