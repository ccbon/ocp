package org.ocpteam.ui.swt;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.TableItem;
import org.ocpteam.misc.JLG;


public class OpenFileAction extends Action {

	private ExplorerComposite composite;


	public OpenFileAction(ExplorerComposite explorerComposite) {
		this.composite = explorerComposite;
		setText("&Open@Enter");
		setToolTipText("Open");
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
		JLG.debug("Open");
		TableItem item = composite.localDirectoryTable.getSelection()[0];
		composite.openLocalFile(item);
	}
}