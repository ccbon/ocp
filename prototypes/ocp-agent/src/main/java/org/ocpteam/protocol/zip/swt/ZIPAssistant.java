package org.ocpteam.protocol.zip.swt;

import org.eclipse.jface.wizard.IWizard;
import org.ocpteam.ui.swt.SWTAgentAssistant;

public class ZIPAssistant extends SWTAgentAssistant {
	@Override
	public IWizard getConnectActionWizardInstance() {
		return new ZipConnectWizard(agent);
	}
}
