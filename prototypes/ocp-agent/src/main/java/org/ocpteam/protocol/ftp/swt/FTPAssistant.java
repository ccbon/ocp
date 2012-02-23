package org.ocpteam.protocol.ftp.swt;

import org.eclipse.jface.wizard.IWizard;
import org.ocpteam.ui.swt.SWTAgentAssistant;

public class FTPAssistant extends SWTAgentAssistant {
	@Override
	public IWizard getConnectActionWizardInstance() {
		return new FTPConfigWizard(agent);
	}
}
