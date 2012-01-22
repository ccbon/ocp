package com.guenego.storage.gui.console;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;

import com.guenego.misc.JLG;

public class ViewContactTabAction extends Action {
	private AdminConsole window;

	public ViewContactTabAction(AdminConsole adminConsole) {
		window = adminConsole;
		setText("Cont&act@Ctrl+T");
		setToolTipText("View Contact Tab");
		try {
			ImageDescriptor i = ImageDescriptor.createFromImageData(new ImageData(ViewContactTabAction.class.getResourceAsStream("view_contact.png")));
			setImageDescriptor(i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		JLG.debug("View Contact");
		window.addContactTab();
	}
}