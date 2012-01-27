package org.guenego.storage.gui;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.guenego.misc.JLG;

public class PasteAction extends Action {

	private AdminConsole window;

	public PasteAction(AdminConsole w) {
		window = w;
		setText("&Paste@Ctrl+V");
		setToolTipText("Paste");
		try {
			ImageDescriptor i = ImageDescriptor
					.createFromImageData(new ImageData(PasteAction.class
							.getResourceAsStream("paste.gif")));
			setImageDescriptor(i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		JLG.debug("Paste");
		if (window.userExplorerComposite == null) {
			return;
		}
		Display display = window.getShell().getDisplay();
		Control c = display.getFocusControl();
		if (c == window.userExplorerComposite.localDirectoryTable) {
			String[] data = (String[]) window.clipboard
					.getContents(FileTransfer.getInstance());
			window.userExplorerComposite.copyFiles(data);
		} else if (c == window.userExplorerComposite.remoteDirectoryTable) {
			String[] data = (String[]) window.clipboard
					.getContents(FileTransfer.getInstance());
			window.userExplorerComposite.commitFiles(data);
		}

	}

	public boolean canRun() {
		if (window.userExplorerComposite == null) {
			return false;
		}
		String[] data = null;
		Display display = window.getShell().getDisplay();
		Control c = display.getFocusControl();
		if (c == window.userExplorerComposite.localDirectoryTable) {
			data = (String[]) window.clipboard.getContents(FileTransfer
					.getInstance());

		} else if (c == window.userExplorerComposite.remoteDirectoryTable) {
			data = (String[]) window.clipboard.getContents(FileTransfer
					.getInstance());
		}
		if (data == null) {
			return false;
		}
		return (data.length > 0);
	}
}