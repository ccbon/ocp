package org.ocpteam.rsp.gui;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.ocpteam.misc.JLG;

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
			if (data != null) {
				window.userExplorerComposite.copyFiles(data);
			}
			String o = (String) window.clipboard.getContents(TextTransfer
					.getInstance());
			String[] s = o.split(";");
			window.userExplorerComposite.checkout(s);
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
			if (data == null || data.length == 0) {
				String s = (String) window.clipboard.getContents(TextTransfer
						.getInstance());
				if (s == null) {
					return false;
				}
				return s.length() > 0;
			}
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