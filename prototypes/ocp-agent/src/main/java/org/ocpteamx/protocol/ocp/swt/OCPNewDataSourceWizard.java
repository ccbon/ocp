package org.ocpteamx.protocol.ocp.swt;

import java.util.Properties;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.ocpteam.misc.LOG;
import org.ocpteam.misc.swt.QuickMessage;
import org.ocpteam.ui.swt.DataSourceWindow;
import org.ocpteam.ui.swt.IScenario;
import org.ocpteamx.protocol.ocp.OCPDataSource;

public class OCPNewDataSourceWizard extends Wizard implements IScenario {

	public boolean bIsFirstAgent = false;
	public String listenerPort = "22222";
	private DataSourceWindow w;
	private boolean bSucceeded;

	public OCPNewDataSourceWizard() {
		setWindowTitle("New Wizard");
	}

	@Override
	public void addPages() {
		addPage(new FirstWizardPage());
		addPage(new NetworkWizardPage());
	}

	@Override
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
		try {
			Properties p = new Properties();
			FirstWizardPage firstPage = (FirstWizardPage) getPage("firstPage");

			p.setProperty("name", firstPage.agentNameText.getText());
			p.setProperty("server", "yes");
			p.setProperty("server.listener.1", "tcp://localhost:"
					+ firstPage.listenerPortText.getText());

			if (bIsFirstAgent) {
				p.setProperty("agent.isFirst", "yes");
			} else {
				p.setProperty("sponsor.1", firstPage.sponsorText.getText());
			}

			if (firstPage.btnCheckButton.getSelection()) {
				p.setProperty("network.type", "public");
				p.setProperty("network.sponsor.url",
						firstPage.sponsorPublicServerText.getText());
			} else {
				p.setProperty("network.type", "private");
			}

			if (bIsFirstAgent) {
				// TODO: review this later...
				// merge this with ocp file
				// prefix property name with network.
				NetworkWizardPage networkPage = (NetworkWizardPage) getPage("networkPage");
				p.setProperty("network.hash",
						networkPage.messageDigestCombo.getText());
				p.setProperty("network.PKAlgo",
						networkPage.asymmetricAlgoCombo.getText());
				p.setProperty("network.backupNbr",
						networkPage.backupNbrText.getText());
				p.setProperty("network.SignatureAlgo", "SHA1withDSA");
				p.setProperty("network.user.cipher.algo", "PBEWithMD5AndDES");

			}
			((OCPDataSource) w.ds).setConfig(p);
		} catch (Exception e) {
			LOG.error(e);
			QuickMessage.error(getShell(), e.getMessage());
			return false;
		}
		return true;
	}

	@Override
	public boolean performCancel() {
		w.ds = null;
		return super.performCancel();
	}

	@Override
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

	@Override
	public void run() throws Exception {
		Display display = w.getShell().getDisplay();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		WizardDialog dialog = new WizardDialog(shell, this);
		bSucceeded = (dialog.open() == 0);
		shell.dispose();
	}

	@Override
	public void setWindow(DataSourceWindow w) {
		this.w = w;

	}

	@Override
	public boolean succeeded() {
		return bSucceeded;
	}

}
