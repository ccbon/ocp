package org.ocpteam.protocol.ftp.swt;

import java.util.Properties;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.ocpteam.component.Agent;


public class FTPConfigWizard extends Wizard {


	public FTPConfigWizard(Agent agent) {
		setWindowTitle("FTP Wizard");
	}

	@Override
	public void addPages() {
		addPage(new FirstWizardPage());
	}

	@Override
	public boolean performFinish() {
		Properties p = new Properties();
		FirstWizardPage firstPage = (FirstWizardPage) getPage("firstPage");
		p.setProperty("hostname", firstPage.serverHostnameText.getText());
		p.setProperty("port", firstPage.portText.getText());
		p.setProperty("default.dir", firstPage.defaultLocalDirText.getText());
		//agent.cfg.loadProperties(p);
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
