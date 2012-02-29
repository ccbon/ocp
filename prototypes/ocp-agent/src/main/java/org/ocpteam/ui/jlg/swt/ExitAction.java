package org.ocpteam.ui.jlg.swt;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.swt.QuickMessage;


public class ExitAction extends Action {
	private DataSourceWindow w;
	public boolean isFirstRun = true;
	boolean wantToExit;

	public ExitAction(DataSourceWindow w) {
		this.w = w;
		setText("&Exit@Ctrl+Q");
		setToolTipText("Exit from the application");
		try {
			ImageDescriptor i = ImageDescriptor
					.createFromImageData(new ImageData(ExitAction.class
							.getResourceAsStream("exit.png")));
			setImageDescriptor(i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		JLG.debug("Exit");
		
		try {
			wantToExit = false;
			if (!QuickMessage.confirm(w.getShell(), "Are you sure you want to exit?")) {
				return;
			}
			wantToExit = true;
			isFirstRun = false;
			if (w.ds != null) {
				try {
					w.ds.getAgent().disconnect();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			JLG.debug("calling close from exit action");
			w.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}