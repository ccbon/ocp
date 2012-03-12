package org.ocpteam.protocol.ocp.swt;

import java.util.Properties;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.swt.QuickMessage;
import org.ocpteam.protocol.ocp.OCPDataSource;
import org.ocpteam.ui.swt.DataSourceWindow;
import org.ocpteam.ui.swt.Scenario;

public class OCPNewDataSourceWizard extends Wizard implements Scenario {

	public boolean bIsFirstAgent = false;
	public String listenerPort = "22222";
	private DataSourceWindow w;

	public OCPNewDataSourceWizard() {
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
		try {
			Properties p = new Properties();
			FirstWizardPage firstPage = (FirstWizardPage) getPage("firstPage");

			p.setProperty("name", firstPage.agentNameText.getText());
			p.setProperty("server", "yes");
			p.setProperty("server.listener.1", "tcp://localhost:"
					+ firstPage.listenerPortText.getText());

			if (bIsFirstAgent) {
				p.setProperty("server.isFirstAgent", "yes");
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
			((OCPDataSource) w.ds).setProperties(p);

			if (bIsFirstAgent) {
				// TODO: review this later...
				// merge this with ocp file
				// prefix property name with network.
				NetworkWizardPage networkPage = (NetworkWizardPage) getPage("networkPage");
				Properties np = new Properties();
				np.setProperty("hash", networkPage.messageDigestCombo.getText());
				np.setProperty("PKAlgo",
						networkPage.asymmetricAlgoCombo.getText());
				np.setProperty("backupNbr", networkPage.backupNbrText.getText());
				np.setProperty("SignatureAlgo", "SHA1withDSA");
				np.setProperty("user.cipher.algo", "PBEWithMD5AndDES");

				JLG.storeConfig(np, "network.properties");

			}
		} catch (Exception e) {
			JLG.error(e);
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
		dialog.open();
		shell.dispose();
	}

	@Override
	public void setWindow(DataSourceWindow w) {
		this.w = w;

	}

}
