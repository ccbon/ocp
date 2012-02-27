package org.ocpteam.ui.swt;

import java.util.Properties;

import org.eclipse.jface.wizard.Wizard;
import org.ocpteam.misc.JLG;

public class ChooseProtocolWizard extends Wizard {

	private ChooseProtocolWizardPage p1;

	public ChooseProtocolWizard() {
		setWindowTitle("New Wizard");
	}

	@Override
	public void addPages() {
		p1 = new ChooseProtocolWizardPage();
		addPage(p1);
	}

	@Override
	public boolean performFinish() {
		Properties p = new Properties();
		p.setProperty("agent.class", p1.selection);
		JLG.storeConfig(p, "protocol.properties");
		return true;
	}

}
