package com.guenego.ocp.gui.console;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import com.guenego.misc.JLG;
import com.guenego.ocp.Agent;

public class RemoveStorageAction extends Action {
	private Agent agent;
	private Display display;

	public RemoveStorageAction(Agent a, Display d) {
		agent = a;
		display = d;
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
		MessageBox messageBox = new MessageBox(new Shell(display),
				SWT.ICON_WARNING | SWT.YES | SWT.NO);
		messageBox
				.setMessage("This will destroy the storage of this agent. Are you sure ?");
		messageBox.setText("Warning");
		int buttonID = messageBox.open();
		switch (buttonID) {
		case SWT.YES:
			agent.storage.removeAll();
		case SWT.NO:
			// exits here ...
			break;
		}
	}
}