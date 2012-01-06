package com.guenego.ocp.gui.console;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import com.guenego.misc.JLG;

public class CommitAction extends Action {

	private Display display;

	public CommitAction(Display d) {
		display = d;
		setText("&About@Ctrl+F1");
		setToolTipText("About");
		try {
			ImageDescriptor i = ImageDescriptor
					.createFromImageData(new ImageData(CommitAction.class
							.getResourceAsStream("about.png")));
			setImageDescriptor(i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		JLG.debug("About");
		MessageBox messageBox = new MessageBox(new Shell(display),
				SWT.ICON_INFORMATION | SWT.OK);
		String message = "Commit Action... to be implemented";
		messageBox.setMessage(message);
		messageBox.setText("Commit");
		messageBox.open();

	}
}