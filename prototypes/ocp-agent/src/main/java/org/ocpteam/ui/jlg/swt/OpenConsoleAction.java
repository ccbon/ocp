package org.ocpteam.ui.jlg.swt;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.ocpteam.misc.JLG;


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

	public void run() {
		JLG.debug("Opening Console");
		window.open();
	}

}
