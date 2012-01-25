package org.guenego.storage.gui;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.guenego.misc.JLG;


public class CutAction extends Action {

	private AdminConsole window;

	public CutAction(AdminConsole w) {
		window = w;
		setText("&Cut@Ctrl+X");
		setToolTipText("Cut");
		try {
			ImageDescriptor i = ImageDescriptor
					.createFromImageData(new ImageData(CutAction.class
							.getResourceAsStream("cut.gif")));
			setImageDescriptor(i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		JLG.debug("Cut");
	}
}