package org.ocpteam.ui.swt.action;

import org.eclipse.jface.action.Action;
import org.ocpteam.misc.LOG;
import org.ocpteam.ui.swt.composite.ExplorerComposite;


public class CreateNewRemoteDirAction extends Action {

	private ExplorerComposite composite;


	public CreateNewRemoteDirAction(ExplorerComposite explorerComposite) {
		this.composite = explorerComposite;
		setText("&New folder@Ctrl+N");
		setToolTipText("New folder");
//		try {
//			ImageDescriptor i = ImageDescriptor
//					.createFromImageData(new ImageData(DataSourceWindow.class
//							.getResourceAsStream("about.png")));
//			setImageDescriptor(i);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}


	@Override
	public void run() {
		LOG.info("Create New Directory");
		composite.createNewRemoteDir();
	}
}