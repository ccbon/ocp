package org.ocpteam.ui.swt;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.ocpteam.misc.LOG;


public class OpenConsoleAction extends Action {

	private DataSourceWindow window;

	public OpenConsoleAction(DataSourceWindow w) {
		window = w;
		setText("&Open Console@Ctrl+O");
		setToolTipText("Open the OCP Agent Administration Console");
		try {
			ImageDescriptor i = ImageDescriptor
					.createFromImageData(new ImageData(ExitAction.class
							.getResourceAsStream("console.png")));
			setImageDescriptor(i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		LOG.debug("Opening Console");
		window.open();
	}

}
