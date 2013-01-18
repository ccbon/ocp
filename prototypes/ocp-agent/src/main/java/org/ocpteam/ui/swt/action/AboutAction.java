package org.ocpteam.ui.swt.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.ocpteam.misc.LOG;
import org.ocpteam.ui.swt.DataSourceWindow;


public class AboutAction extends Action {

	private DataSourceWindow window;

	public AboutAction(DataSourceWindow w) {
		window = w;
		setText("&About@Ctrl+F1");
		setToolTipText("About");
		try {
			ImageDescriptor i = ImageDescriptor
					.createFromImageData(new ImageData(DataSourceWindow.class
							.getResourceAsStream("about.png")));
			setImageDescriptor(i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		LOG.debug("About");
		Display display = window.getShell().getDisplay();
		MessageBox messageBox = new MessageBox(new Shell(display),
				SWT.ICON_INFORMATION | SWT.OK);
		String message = "Remote Storage Agent Prototype v1\n";
		message += "Jean-Louis GUENEGO - JLG Consulting\n";
		message += "Supelec Alcatel Lucent Chair on Flexible Radio @ 2011-2012\n";
		
		messageBox.setMessage(message);
		messageBox.setText("About");
		messageBox.open();
		window.getShell().setFocus();
	}
}