package org.ocpteam.protocol.zip.swt;

import java.util.Properties;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.ocpteam.layer.rsp.Agent;
import org.ocpteam.misc.JLG;


public class ZipConnectWizard extends Wizard {
	
	private Agent agent;

	public ZipConnectWizard(Agent agent) {
		setWindowTitle("FTP Wizard");
		this.agent = agent;
	}

	@Override
	public void addPages() {
		addPage(new ZipConnectWizardPage());
	}

	@Override
	public boolean performFinish() {
		Properties p = new Properties();
		ZipConnectWizardPage firstPage = (ZipConnectWizardPage) getPage("firstPage");
		p.setProperty("default.dir", firstPage.defaultLocalDirText.getText());
		JLG.storeConfig(p, agent.cfg.getConfigFile().getAbsolutePath());
		return true;
	}

	public boolean canFinish() {
		IWizardPage[] pages = getPages();
		for (int i = 0; i < pages.length; i++) {
			if (!pages[i].isPageComplete()) {
				return false;
			}
		}
		return true;
	}

}
