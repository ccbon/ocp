package org.ocpteam.protocol.ocp.swt;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.ocpteam.component.Agent;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.swt.QuickMessage;
import org.ocpteam.protocol.ocp.OCPAgent;
import org.ocpteam.ui.swt.DataSourceWindow;


public class RemoveStorageAction extends Action {
	private DataSourceWindow window;

	public RemoveStorageAction(DataSourceWindow window) {
		this.window = window;
		setText("&Remove local storage");
		setToolTipText("Remove local storage (Test purpose)");
		try {
			ImageDescriptor i = ImageDescriptor.createFromImageData(new ImageData(DataSourceWindow.class.getResourceAsStream("remove_storage.png")));
			setImageDescriptor(i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		JLG.debug("Removing storage...");
		if (QuickMessage.confirm(window.getShell(), "This will destroy the storage of this agent. Are you sure ?")) {
			OCPAgent agent = (OCPAgent) window.ds.getDesigner().get(Agent.class);
			agent.removeStorage();
		}
		window.getShell().setFocus();
	}
}