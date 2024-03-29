package org.ocpteamx.protocol.sftp.swt;

import java.io.File;
import java.net.URI;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.ocpteam.interfaces.IAuthenticable;
import org.ocpteam.interfaces.IUserManagement;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.LOG;
import org.ocpteam.misc.swt.QuickMessage;
import org.ocpteam.ui.swt.DataSourceWindow;
import org.ocpteam.ui.swt.IScenario;
import org.ocpteamx.protocol.sftp.SFTPDataSource;
import org.ocpteamx.protocol.sftp.SSHChallenge;

public class SFTPNewDataSourceWizard extends Wizard implements IScenario {

	private SSHSignInWizardPage p1;
	private DataSourceWindow w;
	private boolean bSucceeded;

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
		LOG.info("sign in user");
		try {
			w.ds = new SFTPDataSource();
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
			URI uri = new URI("sftp://" + p1.sessionText.getText());
			w.ds.setURI(uri);
			IUserManagement um = w.ds.getComponent(IUserManagement.class);
			um.setUsername(p1.sessionText.getText());
			w.ds.getComponent(IAuthenticable.class).setChallenge(c);
		} catch (Exception e) {
			e.printStackTrace();
			QuickMessage.error(getShell(), "Cannot connect");
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
		return p1.isPageComplete();
	}

	@Override
	public void run() throws Exception {
		Display display = w.getShell().getDisplay();
		final Shell shell = new Shell(display);
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
