package org.ocpteam.ui.swt;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.swt.graphics.ImageData;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.swt.QuickMessage;


public class ConnectAgentAction extends Action {
	private AdminConsole w;

	public ConnectAgentAction(AdminConsole w) {
		this.w = w;
		setText("Connect@Ctrl+1");
		setToolTipText("Connect " + w.agent.getProtocolName() + " Agent");
		try {
			ImageDescriptor i = ImageDescriptor
					.createFromImageData(new ImageData(ConnectAgentAction.class
							.getResourceAsStream("start_agent.png")));
			setImageDescriptor(i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		JLG.debug("Connect");
		try {
			if (w.agent.isConnected()) {
				QuickMessage.error(w.getShell(), "Agent already connected.");
			} else {
				SWTAgentAssistant a = (SWTAgentAssistant) w.agent.getAssistant(Main.SWT_ASSISTANT);
				IWizard wizard = a.getConnectActionWizardInstance();
				if (a.startWizard(w.display, wizard) != 0) {
					return;
				}
				w.agent.cfg.loadConfigFile();
				w.agent.readConfig();
				w.agent.connect();
				if (!w.agent.usesAuthentication()) {
					w.bIsAuthenticated = true;
					w.addSyncTab();
					w.addExplorerTab();
				}
				w.updateActions();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}