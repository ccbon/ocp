package com.guenego.storage.gui;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.MessageBox;

import com.guenego.misc.JLG;
import com.guenego.storage.Agent;

public class RemoveStorageAction extends Action {
	private Agent agent;
	private AdminConsole window;

	public RemoveStorageAction(AdminConsole window) {
		this.window = window;
		agent = window.agent;
		setText("&Remove local storage");
		setToolTipText("Remove local storage (Test purpose)");
		try {
			ImageDescriptor i = ImageDescriptor.createFromImageData(new ImageData(RemoveStorageAction.class.getResourceAsStream("remove_storage.png")));
			setImageDescriptor(i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		JLG.debug("Removing storage...");
		MessageBox messageBox = new MessageBox(window.getShell(),
				SWT.ICON_WARNING | SWT.YES | SWT.NO);
		messageBox
				.setMessage("This will destroy the storage of this agent. Are you sure ?");
		messageBox.setText("Warning");
		int buttonID = messageBox.open();
		switch (buttonID) {
		case SWT.YES:
			agent.removeStorage();
			
		case SWT.NO:
			// exits here ...
			break;
		}
		window.getShell().setFocus();
	}
}