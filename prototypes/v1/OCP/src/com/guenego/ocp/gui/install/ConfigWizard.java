package com.guenego.ocp.gui.install;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.guenego.misc.JLG;

public class ConfigWizard extends Wizard {

	private boolean bIsOnlyClient = false;
	private String listenerURL = "tcp://localhost:22220";
	private boolean bIsFirstAgent = false;
	private String sponsorURL = "tcp://localhost:22222";

	public static void start() {
		JLG.debug_on();
		JLG.debug("starting wizard");
		Display display = Display.getDefault();

		final Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());

		WizardDialog dialog = new WizardDialog(shell, new ConfigWizard());
		dialog.open();
//		while (!shell.isDisposed()) {
//			if (!display.readAndDispatch()) {
//				JLG.debug("sleep...");	
//				display.sleep();
//			}
//		}
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
		addPage(new SecondWizardPage());
		addPage(new NetworkWizardPage());
	}

	public IWizardPage getNextPage(IWizardPage page) {
		if (page.getName().equals("firstPage")) {
			return getPage("secondPage");
		} else if (page.getName().equals("secondPage")) {
			SecondWizardPage secondPage = (SecondWizardPage) page;
			bIsFirstAgent = !secondPage.joinButton.getSelection();
			NetworkWizardPage networkPage = (NetworkWizardPage) getPage("networkPage");
			if (bIsFirstAgent) {
				return networkPage;
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

		if (firstPage != null) {
			bIsOnlyClient = firstPage.onlyClientButton.getSelection();
			listenerURL = firstPage.listenerURLText.getText();
		}
		SecondWizardPage secondPage = (SecondWizardPage) getPage("secondPage");
		if (secondPage != null) {
			bIsFirstAgent = !secondPage.joinButton.getSelection();
			sponsorURL = secondPage.sponsorURLText.getText();
		} else {
			System.out.println("second page is null");
		}

		if (bIsOnlyClient) {
			p.setProperty("server", "no");
		} else {
			p.setProperty("server", "yes");
			p.setProperty("server.listener.1", listenerURL);
		}

		if (bIsFirstAgent) {
			p.setProperty("server.isFirstAgent", "yes");
		} else {
			p.setProperty("sponsor.1", sponsorURL);
		}

		OutputStream out = null;
		try {
			out = new FileOutputStream(new File("agent.properties"));
			p.store(out, "no comment");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

		if (bIsFirstAgent) {
			NetworkWizardPage networkPage = (NetworkWizardPage) getPage("networkPage");
			Properties np = new Properties();
			np.setProperty("hash", networkPage.messageDigestCombo.getText());
			np.setProperty("PKAlgo", networkPage.asymmetricAlgoCombo.getText());
			np.setProperty("backupNbr", networkPage.backupNbrText.getText());
			np.setProperty("SignatureAlgo", "SHA1withDSA");
			np.setProperty("user.cipher.algo", "PBEWithMD5AndDES");
			

			try {
				out = new FileOutputStream(new File("network.properties"));
				np.store(out, "no comment");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					out.close();
				} catch (Exception e) {
					// TODO: handle exception
				}
			}

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
