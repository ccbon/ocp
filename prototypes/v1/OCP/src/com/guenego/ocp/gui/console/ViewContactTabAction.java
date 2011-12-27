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

public class ViewContactTabAction extends Action {
	private Agent agent;
	private Display display;
	private AdminConsole window;

	public ViewContactTabAction(Agent a, Display d, AdminConsole adminConsole) {
		agent = a;
		display = d;
		window = adminConsole;
		setText("Cont&act@Ctrl+A");
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