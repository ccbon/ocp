package org.ocpteam.ui.swt;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Shell;
import org.ocpteam.component.Authentication;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.swt.QuickMessage;


public class SignInWizard extends Wizard {

	public static void start(final DataSourceWindow window) {
		final Shell shell = new Shell(window.getShell().getDisplay());
		shell.setLayout(new FillLayout());

		WizardDialog dialog = new WizardDialog(shell, new SignInWizard(window)) {
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
	private DataSourceWindow window;

	public SignInWizard(DataSourceWindow window) {
		this.window = window;
		setWindowTitle("Sign In Wizard");
	}

	@Override
	public void addPages() {
		p1 = new SignInWizardPage(window);
		addPage(p1);
	}

	@Override
	public boolean performFinish() {
		JLG.debug("sign in user");
		try {
			Authentication a = window.ds.getComponent(Authentication.class);
			a.setLogin(p1.usernameText.getText());
			a.setChallenge(p1.passwordText.getText());
			window.signIn();
		} catch (Exception e) {
			JLG.error(e);
			QuickMessage.error(window.getShell(), "Sign in failed: " + e.getMessage());
		}
		return true;
	}

	@Override
	public boolean canFinish() {
		return p1.isPageComplete();
	}
}
