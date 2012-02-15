package org.ocpteam.storage.gui;

import java.util.Map;
import java.util.Properties;

import org.eclipse.jface.wizard.Wizard;
import org.ocpteam.misc.JLG;

public class ChooseProtocolWizard extends Wizard {

	private ChooseProtocolWizardPage p1;
	private Map<String, String> map;

	public ChooseProtocolWizard(Map<String, String> map) {
		setWindowTitle("New Wizard");
		this.map = map;
	}

	@Override
	public void addPages() {
		p1 = new ChooseProtocolWizardPage(map);
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
