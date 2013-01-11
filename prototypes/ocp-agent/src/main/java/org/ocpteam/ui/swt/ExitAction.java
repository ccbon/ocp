package org.ocpteam.ui.swt;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.ocpteam.misc.LOG;
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

	@Override
	public void run() {
		LOG.debug("Exit");
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