package org.ocpteam.ui.swt;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.ocpteam.layer.rsp.Agent;
import org.ocpteam.misc.JLG;

public class SWTAgentAssistant {

	public Agent agent;

	public void startWizard(Display display, IWizard wizard) {
		if (wizard != null) {
			final Shell shell = new Shell(display);
			shell.setLayout(new FillLayout());
			WizardDialog dialog = new WizardDialog(shell, wizard);
			dialog.open();
			shell.dispose();
		}
	}

	public static Object getInstance(Agent agent) {
		String packageName = agent.getClass().getPackage().getName();
		JLG.debug("packageName=" + packageName);
		SWTAgentAssistant result;
		try {
			result = (SWTAgentAssistant) Class.forName(
					packageName + ".swt." + agent.getProtocolName()
							+ "Assistant").newInstance();
		} catch (Exception e) {
			result = new SWTAgentAssistant();
		}
		result.agent = agent;
		return result;
	}

	public IWizard getConnectActionWizardInstance() {
		return null;
	}

	public IWizard getConfigWizardInstance() {
		return null;
	}
}
