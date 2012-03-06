package org.ocpteam.protocol.sftp.swt;

import java.io.File;
import java.net.URI;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.ocpteam.layer.rsp.DataSource;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.swt.QuickMessage;
import org.ocpteam.protocol.sftp.SFTPDataSource;
import org.ocpteam.protocol.sftp.SSHChallenge;
import org.ocpteam.ui.swt.DataSourceWindow;
import org.ocpteam.ui.swt.Scenario;

public class SFTPNewDataSourceWizard extends Wizard implements
		Scenario {

	private SSHSignInWizardPage p1;

	public DataSource ds;

	public SFTPNewDataSourceWizard() {
		setWindowTitle("SSH Sign In Wizard");
	}

	@Override
	public void addPages() {
		p1 = new SSHSignInWizardPage();
		addPage(p1);
	}

	@Override
	public boolean performFinish() {
		JLG.debug("sign in user");
		try {
			SSHChallenge c = new SSHChallenge();
			c.setDefaultLocalDir(p1.dirText.getText());
			if (p1.bIsPassword) {
				c.setType(SSHChallenge.PASSWORD);
				c.setPassword(p1.passwordText.getText());
			} else {
				c.setType(SSHChallenge.PRIVATE_KEY);
				// TODO : test if the file exists.
				c.setPrivateKeyFile(new File(p1.privateKeyFileText.getText()));
				String passphrase = p1.passphraseText.getText();
				if (!JLG.isNullOrEmpty(passphrase)) {
					c.setPassphrase(passphrase);
				}
			}
			ds = new SFTPDataSource(new URI("sftp://"
					+ p1.sessionText.getText()), c);
		} catch (Exception e) {
			e.printStackTrace();
			QuickMessage.error(getShell(), "Cannot connect");
		}

		return true;
	}

	public boolean canFinish() {
		return p1.isPageComplete();
	}

	@Override
	public void run(DataSourceWindow w) throws Exception {
		Display display = Display.getDefault();
		final Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		WizardDialog dialog = new WizardDialog(shell, this);
		dialog.open();
		shell.dispose();
		w.ds = ds;
	}
}