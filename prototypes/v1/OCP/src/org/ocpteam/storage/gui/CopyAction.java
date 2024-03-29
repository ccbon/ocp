package org.ocpteam.storage.gui;

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

public class CopyAction extends Action {

	private AdminConsole window;

	public CopyAction(AdminConsole w) {
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

	public void run() {
		JLG.debug("Copy");
		// copy only from the local directory
		if (window.userExplorerComposite == null) {
			return;
		}
		Display display = window.getShell().getDisplay();
		Control c = display.getFocusControl();
		if (c == window.userExplorerComposite.localDirectoryTable) {
			Table t = (Table) c;
			int length = t.getSelectionCount();
			String[] data = new String[length];
			for (int i = 0; i < length; i++) {
				TableItem item = t.getSelection()[i];
				String name = item.getText(0);
				File f = new File(
						window.userExplorerComposite.currentLocalDirectory,
						name);
				String path = f.getAbsolutePath();
				JLG.debug("path=" + path);
				data[i] = path;
			}
			window.clipboard.setContents(new Object[] { data },
					new Transfer[] { FileTransfer.getInstance() });
		}
		if (c == window.userExplorerComposite.remoteDirectoryTable) {
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
		if (window.userExplorerComposite == null) {
			return false;
		}
		Display display = window.getShell().getDisplay();
		Control c = display.getFocusControl();
		if (c.getClass() == Table.class) {
			Table t = (Table) c;
			int length = t.getSelectionCount();
			return (length > 0);
		} else {
			return false;
		}
	}
}