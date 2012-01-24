package org.guenego.storage.gui;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.guenego.misc.JLG;


public class StopAgentAction extends Action {
	private AdminConsole w;

	public StopAgentAction(AdminConsole w) {
		this.w = w;
		setText("Stop Agent@Ctrl+2");
		setToolTipText("Stop " + w.agent.getProtocolName() + " Agent");
		try {
			ImageDescriptor i = ImageDescriptor
					.createFromImageData(new ImageData(StopAgentAction.class
							.getResourceAsStream("stop_agent.png")));
			setImageDescriptor(i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		JLG.debug("Stop Agent");
		try {
			if (!w.agent.isStarted()) {
				QuickMessage.error(w.getShell(), "Agent not started.");
			} else {
				w.setUser(null);
				w.agent.stop();
				w.updateActions();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}