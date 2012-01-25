package org.guenego.storage.gui;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.guenego.misc.JLG;


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
		if (window.userExplorerComposite == null) {
			return;
		}
		Display display = window.getShell().getDisplay();
		Control c = display.getFocusControl();
		if (c == window.userExplorerComposite.localDirectoryTable) {
			String data = (String) window.clipboard.getContents(TextTransfer.getInstance());
			JLG.debug("data=" + data);
		}

	}
}