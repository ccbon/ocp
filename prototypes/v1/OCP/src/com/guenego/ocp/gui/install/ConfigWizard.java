package com.guenego.ocp.gui.install;

import java.util.Properties;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.guenego.misc.JLG;
import com.guenego.ocp.Agent;

public class ConfigWizard extends Wizard {

	
	public boolean bIsFirstAgent = false;
	public String listenerPort = "22222";

	public static void start() {
		JLG.debug_on();
		JLG.debug("starting wizard");
		Display display = Display.getDefault();

		final Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());

		WizardDialog dialog = new WizardDialog(shell, new ConfigWizard());
		dialog.open();

		JLG.debug("about to dispose");
		shell.dispose();
		display.dispose();
	}

	public ConfigWizard() {
		setWindowTitle("New Wizard");
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
		JLG.storeConfig(p, Agent.AGENT_PROPERTIES_FILE);

		if (bIsFirstAgent) {
			NetworkWizardPage networkPage = (NetworkWizardPage) getPage("networkPage");
			Properties np = new Properties();
			np.setProperty("hash", networkPage.messageDigestCombo.getText());
			np.setProperty("PKAlgo", networkPage.asymmetricAlgoCombo.getText());
			np.setProperty("backupNbr", networkPage.backupNbrText.getText());
			np.setProperty("SignatureAlgo", "SHA1withDSA");
			np.setProperty("user.cipher.algo", "PBEWithMD5AndDES");

			JLG.storeConfig(np, Agent.NETWORK_PROPERTIES_FILE);

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
