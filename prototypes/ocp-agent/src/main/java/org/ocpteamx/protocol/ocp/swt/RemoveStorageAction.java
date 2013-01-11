package org.ocpteamx.protocol.ocp.swt;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.ocpteam.component.Agent;
import org.ocpteam.misc.LOG;
import org.ocpteam.misc.swt.QuickMessage;
import org.ocpteam.ui.swt.DataSourceWindow;
import org.ocpteamx.protocol.ocp.OCPAgent;


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

	@Override
	public void run() {
		LOG.debug("Removing storage...");
		if (QuickMessage.confirm(window.getShell(), "This will destroy the storage of this agent. Are you sure ?")) {
			OCPAgent agent = (OCPAgent) window.ds.getComponent(Agent.class);
			try {
				agent.removeStorage();
			} catch (Exception e) {
				QuickMessage.error(window.getShell(), e.getMessage());
			}
		}
		window.getShell().setFocus();
	}
}