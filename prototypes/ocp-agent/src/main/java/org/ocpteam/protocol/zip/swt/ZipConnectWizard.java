package org.ocpteam.protocol.zip.swt;

import java.io.File;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.ocpteam.layer.rsp.Agent;
import org.ocpteam.protocol.zip.ZipAgent;


public class ZipConnectWizard extends Wizard {
	private ZipConnectWizardPage p;
	private ZipAgent agent;

	public ZipConnectWizard(Agent agent) {
		setWindowTitle("FTP Wizard");
		this.agent = (ZipAgent) agent;
	}

	@Override
	public void addPages() {
		p = new ZipConnectWizardPage();
		addPage(p);
	}

	@Override
	public boolean performFinish() {
		agent.zipfile = new File(p.fileText.getText());
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
