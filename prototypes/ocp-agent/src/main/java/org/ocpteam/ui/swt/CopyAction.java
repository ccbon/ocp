package org.ocpteam.ui.swt;

import java.io.File;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.LOG;

public class CopyAction extends Action {

	private DataSourceWindow window;

	public CopyAction(DataSourceWindow w) {
		window = w;
		setText("&Copy@Ctrl+C");
		setToolTipText("Copy");
		try {
			ImageDescriptor i = ImageDescriptor
					.createFromImageData(new ImageData(CopyAction.class
							.getResourceAsStream("copy.gif")));
			setImageDescriptor(i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		LOG.debug("Copy");
		// copy only from the local directory
		if (window.explorerComposite == null) {
			return;
		}
		
		if (!(window.explorerComposite instanceof ExplorerComposite)) {
			return;			
		}
		ExplorerComposite explorerComposite = (ExplorerComposite) window.explorerComposite;
		Display display = window.getShell().getDisplay();
		Control c = display.getFocusControl();
		if (c == explorerComposite.localDirectoryTable) {
			Table t = (Table) c;
			int length = t.getSelectionCount();
			String[] data = new String[length];
			for (int i = 0; i < length; i++) {
				TableItem item = t.getSelection()[i];
				String name = item.getText(0);
				File f = new File(
						explorerComposite.currentLocalDirectory,
						name);
				String path = f.getAbsolutePath();
				LOG.debug("path=" + path);
				data[i] = path;
			}
			window.clipboard.setContents(new Object[] { data },
					new Transfer[] { FileTransfer.getInstance() });
		}
		if (c == explorerComposite.remoteDirectoryTable) {
			Table t = (Table) c;
			int length = t.getSelectionCount();
			String[] data = new String[length];
			for (int i = 0; i < length; i++) {
				TableItem item = t.getSelection()[i];
				String name = item.getText(0);
				data[i] = name;
			}
			window.clipboard.setContents(new Object[] { JLG.join(";", (Object[]) data) },
					new Transfer[] { TextTransfer.getInstance() });
		}

	}

	public boolean canRun() {
		if (window.explorerComposite == null) {
			return false;
		}
		Display display = window.getShell().getDisplay();
		Control c = display.getFocusControl();
		if (c != null && c.getClass() == Table.class) {
			Table t = (Table) c;
			int length = t.getSelectionCount();
			return (length > 0);
		} else {
			return false;
		}
	}
}