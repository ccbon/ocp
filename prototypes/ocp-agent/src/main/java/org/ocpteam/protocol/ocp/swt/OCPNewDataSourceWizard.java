package org.ocpteam.protocol.ocp.swt;

import java.io.File;
import java.util.Properties;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.ocpteam.layer.rsp.DataSource;
import org.ocpteam.misc.JLG;
import org.ocpteam.protocol.ocp.OCPDataSource;
import org.ocpteam.ui.swt.DataSourceWindow;
import org.ocpteam.ui.swt.Scenario;


public class OCPNewDataSourceWizard extends Wizard implements Scenario {

	
	public boolean bIsFirstAgent = false;
	public String listenerPort = "22222";
	private DataSource ds;


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
		
		if (firstPage.btnCheckButton.getSelection()) {
			p.setProperty("network.type", "public");
			p.setProperty("network.sponsor.url", firstPage.sponsorPublicServerText.getText());
		} else {
			p.setProperty("network.type", "private");
		}
		ds.setProperties(p);
		try {
			ds.save();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		if (bIsFirstAgent) {
			NetworkWizardPage networkPage = (NetworkWizardPage) getPage("networkPage");
			Properties np = new Properties();
			np.setProperty("hash", networkPage.messageDigestCombo.getText());
			np.setProperty("PKAlgo", networkPage.asymmetricAlgoCombo.getText());
			np.setProperty("backupNbr", networkPage.backupNbrText.getText());
			np.setProperty("SignatureAlgo", "SHA1withDSA");
			np.setProperty("user.cipher.algo", "PBEWithMD5AndDES");

			JLG.storeConfig(np, "network.properties");

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

	@Override
	public void run(DataSourceWindow w) throws Exception {
		Display display = Display.getDefault();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		FileDialog fd = new FileDialog(shell, SWT.SAVE);
		fd.setText("Save");
		fd.setFilterPath(System.getProperty("user.home"));
		String[] filterExt = { "*.ocp", "*.uri", "*.*" };
		fd.setFilterExtensions(filterExt);
		String selected = fd.open();
		JLG.debug(selected);
		if (selected == null) {
			return;
		}
		File file = new File(selected);
		w.ds = new OCPDataSource(file);
		if (!file.exists()) {
			ds = w.ds;
			WizardDialog dialog = new WizardDialog(shell, this);
			dialog.open();			
		}		
		shell.dispose();
	}

}
