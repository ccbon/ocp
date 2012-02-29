package org.ocpteam.ui.swt;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.swt.QuickMessage;


public class DisconnectAgentAction extends Action {
	private AdminConsole w;

	public DisconnectAgentAction(AdminConsole w) {
		this.w = w;
		setText("Disconnect@Ctrl+2");
		setToolTipText("Disconnect " + w.agent.getProtocolName() + " Agent");
		try {
			ImageDescriptor i = ImageDescriptor
					.createFromImageData(new ImageData(DisconnectAgentAction.class
							.getResourceAsStream("stop_agent.png")));
			setImageDescriptor(i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		JLG.debug("Disconnect Agent");
		try {
			if (!w.agent.isConnected()) {
				QuickMessage.error(w.getShell(), "Agent not connected.");
			} else {
				w.setUser(null);
				w.agent.disconnect();
				w.updateActions();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}