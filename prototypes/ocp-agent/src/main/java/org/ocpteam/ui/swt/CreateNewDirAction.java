package org.ocpteam.ui.swt;

import org.eclipse.jface.action.Action;
import org.ocpteam.misc.JLG;


public class CreateNewDirAction extends Action {

	private ExplorerComposite composite;


	public CreateNewDirAction(ExplorerComposite explorerComposite) {
		this.composite = explorerComposite;
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


	@Override
	public void run() {
		JLG.debug("Create New Directory");
		composite.createNewLocalDir();
	}
}