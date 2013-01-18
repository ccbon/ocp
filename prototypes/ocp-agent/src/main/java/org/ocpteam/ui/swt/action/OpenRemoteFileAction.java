package org.ocpteam.ui.swt.action;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.TableItem;
import org.ocpteam.misc.LOG;
import org.ocpteam.ui.swt.composite.ExplorerComposite;


public class OpenRemoteFileAction extends Action {

	private ExplorerComposite composite;


	public OpenRemoteFileAction(ExplorerComposite explorerComposite) {
		this.composite = explorerComposite;
		setText("&Open");
		setToolTipText("Open remote file");
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
		LOG.debug("Open");
		TableItem item = composite.remoteDirectoryTable.getSelection()[0];
		composite.openRemoteFile(item);
	}
}