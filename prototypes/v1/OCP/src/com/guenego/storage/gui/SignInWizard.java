package com.guenego.storage.gui;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import com.guenego.misc.JLG;
import com.guenego.storage.Agent;
import com.guenego.storage.User;

public class SignInWizard extends Wizard {

	public static void start(Display display, Agent agent,
			final AdminConsole window) {
		final Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());

		WizardDialog dialog = new WizardDialog(shell, new SignInWizard(agent,
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
		// shell.dispose();
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
