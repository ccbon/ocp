package org.ocpteam.ui.jlg.swt;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.ocpteam.misc.JLG;

public class PasteAction extends Action {

	private DataSourceWindow window;

	public PasteAction(DataSourceWindow w) {
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
		if (window.explorerComposite == null) {
			return;
		}
		Display display = window.getShell().getDisplay();
		Control c = display.getFocusControl();
		if (c == window.explorerComposite.localDirectoryTable) {
			String[] data = (String[]) window.clipboard
					.getContents(FileTransfer.getInstance());
			if (data != null) {
				window.explorerComposite.copyFiles(data);
			}
			String o = (String) window.clipboard.getContents(TextTransfer
					.getInstance());
			String[] s = o.split(";");
			window.explorerComposite.checkout(s);
		} else if (c == window.explorerComposite.remoteDirectoryTable) {
			String[] data = (String[]) window.clipboard
					.getContents(FileTransfer.getInstance());
			window.explorerComposite.commitFiles(data);
		}

	}

	public boolean canRun() {
		if (window.explorerComposite == null) {
			return false;
		}
		String[] data = null;
		Display display = window.getShell().getDisplay();
		Control c = display.getFocusControl();
		if (c == window.explorerComposite.localDirectoryTable) {
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
		} else if (c == window.explorerComposite.remoteDirectoryTable) {
			data = (String[]) window.clipboard.getContents(FileTransfer
					.getInstance());
		}
		if (data == null) {
			return false;
		}
		return (data.length > 0);

	}
}