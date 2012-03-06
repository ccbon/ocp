package org.ocpteam.protocol.ocp.swt;

import org.eclipse.jface.wizard.IWizard;
import org.ocpteam.protocol.ocp.OCPAgent;
import org.ocpteam.ui.swt.SWTAgentAssistant;

public class OCPAssistant extends SWTAgentAssistant {
	@Override
	public IWizard getConfigWizardInstance() {
		return new ConfigWizard((OCPAgent) agent);
	}
}
