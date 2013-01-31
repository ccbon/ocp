package org.ocpteam.ui.swt.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.ocpteam.misc.LOG;
import org.ocpteam.ui.swt.DataSourceWindow;


public class OpenConsoleAction extends Action {

	private DataSourceWindow window;

	public OpenConsoleAction(DataSourceWindow w) {
		window = w;
		setText("&Open Console@Ctrl+O");
		setToolTipText("Open the OCP Agent Administration Console");
		try {
			ImageDescriptor i = ImageDescriptor
					.createFromImageData(new ImageData(DataSourceWindow.class
							.getResourceAsStream("console.png")));
			setImageDescriptor(i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		LOG.info("Opening Console");
		window.open();
	}

}
