package org.ocpteam.ui.swt;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.ocpteam.layer.rsp.Agent;
import org.ocpteam.layer.rsp.User;
import org.ocpteam.misc.JLG;


public class SignInWizard extends Wizard {

	public static void start(final AdminConsole window) {
		final Shell shell = new Shell(window.getShell().getDisplay());
		shell.setLayout(new FillLayout());

		WizardDialog dialog = new WizardDialog(shell, new SignInWizard(window.agent,
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

	private SignInWizardPage p1;
	private Agent agent;
	private AdminConsole window;

	public SignInWizard(Agent agent, AdminConsole window) {
		this.agent = agent;
		this.window = window;
		setWindowTitle("Sign In Wizard");
	}

	@Override
	public void addPages() {
		p1 = new SignInWizardPage();
		addPage(p1);
	}

	@Override
	public boolean performFinish() {
		JLG.debug("sign in user");
		try {
			User user = agent.login(p1.usernameText.getText(),
					p1.passwordText.getText());
			window.setUser(user);
			window.addUserSyncTab();
			window.addUserExplorerTab();
		} catch (Exception e) {
			JLG.error(e);
			MessageBox messageBox = new MessageBox(getShell(), SWT.ICON_ERROR
					| SWT.OK);
			messageBox.setMessage("Bad login/password.");
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
