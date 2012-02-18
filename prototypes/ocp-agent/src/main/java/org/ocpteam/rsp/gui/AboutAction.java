package org.ocpteam.rsp.gui;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.ocpteam.misc.JLG;


public class AboutAction extends Action {

	private AdminConsole window;

	public AboutAction(AdminConsole w) {
		window = w;
		setText("&About@Ctrl+F1");
		setToolTipText("About");
		try {
			ImageDescriptor i = ImageDescriptor
					.createFromImageData(new ImageData(AboutAction.class
							.getResourceAsStream("about.png")));
			setImageDescriptor(i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		JLG.debug("About");
		Display display = window.getShell().getDisplay();
		MessageBox messageBox = new MessageBox(new Shell(display),
				SWT.ICON_INFORMATION | SWT.OK);
		String message = "Remote Storage Agent Prototype v1\n";
		message += "Protocol Tested: " + window.agent.getProtocolName() + "\n";
		message += "Jean-Louis GUENEGO - JLG Consulting\n";
		message += "Supelec Alcatel Lucent Chair on Flexible Radio @ 2011-2012\n";
		
		messageBox.setMessage(message);
		messageBox.setText("About");
		messageBox.open();
		window.getShell().setFocus();
	}
}