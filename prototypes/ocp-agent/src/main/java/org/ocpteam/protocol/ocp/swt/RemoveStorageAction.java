package org.ocpteam.protocol.ocp.swt;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.ocpteam.layer.dsp.DSPAgent;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.swt.QuickMessage;
import org.ocpteam.ui.jlg.swt.DataSourceWindow;


public class RemoveStorageAction extends Action {
	private DSPAgent agent;
	private DataSourceWindow window;

	public RemoveStorageAction(DataSourceWindow window) {
		this.window = window;
		agent = (DSPAgent) window.agent;
		setText("&Remove local storage");
		setToolTipText("Remove local storage (Test purpose)");
		try {
			ImageDescriptor i = ImageDescriptor.createFromImageData(new ImageData(RemoveStorageAction.class.getResourceAsStream("remove_storage.png")));
			setImageDescriptor(i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		JLG.debug("Removing storage...");
		if (QuickMessage.confirm(window.getShell(), "This will destroy the storage of this agent. Are you sure ?")) {
			agent.removeStorage();
		}
		window.getShell().setFocus();
	}
}