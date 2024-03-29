package org.ocpteam.ui.swt.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.ocpteam.misc.LOG;
import org.ocpteam.misc.swt.QuickMessage;
import org.ocpteam.ui.swt.DataSourceWindow;
import org.ocpteam.ui.swt.editprefpage.GeneralPreferencePage;

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
					.createFromImageData(new ImageData(DataSourceWindow.class
							.getResourceAsStream("exit.png")));
			setImageDescriptor(i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		LOG.info("Exit");
		if (confirm()) {
			exit();
		}
	}

	public boolean exit() {
		return w.exit();
	}

	public boolean confirm() {
		if (w.ps.getBoolean(GeneralPreferencePage.CONFIRM_ON_EXIT)) {
			return QuickMessage.confirm(w.getShell(),
					"Are you sure you want to exit?");
		}
		return true;
	}
}