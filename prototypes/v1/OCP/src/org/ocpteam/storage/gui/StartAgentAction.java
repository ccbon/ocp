package org.ocpteam.storage.gui;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.ocpteam.misc.JLG;


public class StartAgentAction extends Action {
	private AdminConsole w;

	public StartAgentAction(AdminConsole w) {
		this.w = w;
		setText("Start Agent@Ctrl+1");
		setToolTipText("Start " + w.agent.getProtocolName() + " Agent");
		try {
			ImageDescriptor i = ImageDescriptor
					.createFromImageData(new ImageData(StartAgentAction.class
							.getResourceAsStream("start_agent.png")));
			setImageDescriptor(i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		JLG.debug("Start Agent");
		try {
			if (w.agent.isStarted()) {
				QuickMessage.error(w.getShell(), "Agent already started.");
			} else {
				w.agent.loadConfig();
				w.agent.start();
				w.updateActions();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}