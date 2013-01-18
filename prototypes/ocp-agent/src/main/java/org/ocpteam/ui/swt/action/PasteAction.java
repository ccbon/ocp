package org.ocpteam.ui.swt.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.ocpteam.misc.LOG;
import org.ocpteam.ui.swt.DataSourceWindow;
import org.ocpteam.ui.swt.composite.ExplorerComposite;

public class PasteAction extends Action {

	private DataSourceWindow window;

	public PasteAction(DataSourceWindow w) {
		window = w;
		setText("&Paste@Ctrl+V");
		setToolTipText("Paste");
		try {
			ImageDescriptor i = ImageDescriptor
					.createFromImageData(new ImageData(DataSourceWindow.class
							.getResourceAsStream("paste.gif")));
			setImageDescriptor(i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		LOG.debug("Paste");
		if (window.explorerComposite == null) {
			return;
		}
		if (window.explorerComposite instanceof ExplorerComposite) {
			ExplorerComposite explorerComposite = (ExplorerComposite) window.explorerComposite;
			Display display = window.getShell().getDisplay();
			Control c = display.getFocusControl();
			if (c == explorerComposite.localDirectoryTable) {
				String[] data = (String[]) window.clipboard
						.getContents(FileTransfer.getInstance());
				if (data != null) {
					explorerComposite.copyFiles(data);
				}
				String o = (String) window.clipboard.getContents(TextTransfer
						.getInstance());
				String[] s = o.split(";");
				explorerComposite.checkout(s);
			} else if (c == explorerComposite.remoteDirectoryTable) {
				String[] data = (String[]) window.clipboard
						.getContents(FileTransfer.getInstance());
				explorerComposite.commitFiles(data);
			}
		}
	}

	public boolean canRun() {
		if (window.explorerComposite == null) {
			return false;
		}
		if (!(window.explorerComposite instanceof ExplorerComposite)) {
			return false;			
		}
		ExplorerComposite explorerComposite = (ExplorerComposite) window.explorerComposite;

		String[] data = null;
		Display display = window.getShell().getDisplay();
		Control c = display.getFocusControl();
		if (c == explorerComposite.localDirectoryTable) {
			data = (String[]) window.clipboard.getContents(FileTransfer
					.getInstance());
			if (data == null || data.length == 0) {
				String s = (String) window.clipboard.getContents(TextTransfer
						.getInstance());
				if (s == null) {
					return false;
				}
				return s.length() > 0;
			}
		} else if (c == explorerComposite.remoteDirectoryTable) {
			data = (String[]) window.clipboard.getContents(FileTransfer
					.getInstance());
		}
		if (data == null) {
			return false;
		}
		return (data.length > 0);

	}
}