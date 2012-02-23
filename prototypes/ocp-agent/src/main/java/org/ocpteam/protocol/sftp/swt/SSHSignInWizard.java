package org.ocpteam.protocol.sftp.swt;

import java.io.File;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.ocpteam.layer.rsp.Agent;
import org.ocpteam.layer.rsp.User;
import org.ocpteam.misc.JLG;
import org.ocpteam.protocol.sftp.SSHChallenge;
import org.ocpteam.ui.swt.AdminConsole;


public class SSHSignInWizard extends Wizard {

	public static void start(final AdminConsole window) {
		final Shell shell = new Shell(window.getShell().getDisplay());
		shell.setLayout(new FillLayout());

		WizardDialog dialog = new WizardDialog(shell, new SSHSignInWizard(window.agent,
				window)) {
			@Override
			protected void finishPressed() {
				// TODO Auto-generated method stub
				super.finishPressed();
				window.tabFolder.setFocus();
			}
		};
		dialog.open();
		JLG.debug("about to dispose shell");
		shell.dispose();
	}

	private SSHSignInWizardPage p1;
	private Agent agent;
	private AdminConsole window;

	public SSHSignInWizard(Agent agent, AdminConsole window) {
		this.agent = agent;
		this.window = window;
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
				//TODO : test if the file exists.
				c.setPrivateKeyFile(new File(p1.privateKeyFileText.getText()));
				String passphrase = p1.passphraseText.getText();
				if (!JLG.isNullOrEmpty(passphrase)) {
					c.setPassphrase(passphrase);
				}
			}
			User user = agent.login(p1.sessionText.getText(),
					c);
			window.setUser(user);
			window.addSyncTab();
			window.addExplorerTab();
		} catch (Exception e) {
			JLG.error(e);
			MessageBox messageBox = new MessageBox(getShell(), SWT.ICON_ERROR
					| SWT.OK);
			messageBox.setMessage("Bad authentication.");
			messageBox.setText("Warning");
			messageBox.open();
			return false;
		}

		return true;
	}

	public boolean canFinish() {
		return p1.isPageComplete();
	}

	public Agent getAgent() {
		return agent;
	}
}
