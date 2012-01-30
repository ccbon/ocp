package org.ocpteam.ocp.gui.install;

import java.util.Properties;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.ocpteam.misc.JLG;
import org.ocpteam.ocp.OCPAgent;
import org.ocpteam.storage.Agent;


public class ConfigWizard extends Wizard {

	
	public boolean bIsFirstAgent = false;
	public String listenerPort = "22222";
	private Agent agent;


	public ConfigWizard(Agent agent) {
		setWindowTitle("New Wizard");
		this.agent = agent;
	}

	@Override
	public void addPages() {
		addPage(new FirstWizardPage());
		addPage(new NetworkWizardPage());
	}

	public IWizardPage getNextPage(IWizardPage page) {
		if (page.getName().equals("firstPage")) {
			if (bIsFirstAgent) {
				return getPage("networkPage");
			} else {
				return null;
			}
		}

		return super.getNextPage(page);
	}

	@Override
	public boolean performFinish() {
		Properties p = new Properties();
		FirstWizardPage firstPage = (FirstWizardPage) getPage("firstPage");

		p.setProperty("name", firstPage.agentNameText.getText());
		p.setProperty("server", "yes");
		p.setProperty("server.listener.1", "tcp://localhost:" + firstPage.listenerPortText.getText());

		if (bIsFirstAgent) {
			p.setProperty("server.isFirstAgent", "yes");
		} else {
			p.setProperty("sponsor.1", firstPage.sponsorText.getText());
		}
		JLG.storeConfig(p, agent.getConfigFile().getAbsolutePath());

		if (bIsFirstAgent) {
			NetworkWizardPage networkPage = (NetworkWizardPage) getPage("networkPage");
			Properties np = new Properties();
			np.setProperty("hash", networkPage.messageDigestCombo.getText());
			np.setProperty("PKAlgo", networkPage.asymmetricAlgoCombo.getText());
			np.setProperty("backupNbr", networkPage.backupNbrText.getText());
			np.setProperty("SignatureAlgo", "SHA1withDSA");
			np.setProperty("user.cipher.algo", "PBEWithMD5AndDES");

			JLG.storeConfig(np, ((OCPAgent) agent).getNetworkConfigFile().getAbsolutePath());

		}

		return true;
	}

	public boolean canFinish() {
		IWizardPage[] pages = getPages();
		for (int i = 0; i < pages.length; i++) {
			if (!pages[i].isPageComplete()) {
				System.out.println("page not completed: " + i);
				return false;
			}
		}
		System.out.println("all pages completed.");
		return true;
	}

}
