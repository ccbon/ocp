package org.ocpteam.ui.swt.action;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.TableItem;
import org.ocpteam.misc.LOG;
import org.ocpteam.misc.swt.QuickMessage;
import org.ocpteam.ui.swt.composite.ExplorerComposite;


public class RemoveFileAction extends Action {

	private ExplorerComposite composite;


	public RemoveFileAction(ExplorerComposite explorerComposite) {
		this.composite = explorerComposite;
		setText("&Delete@DEL");
		setToolTipText("Delete");
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
		LOG.info("Delete local file");
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