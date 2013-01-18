package org.ocpteam.ui.swt.wizard;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Shell;
import org.ocpteam.interfaces.IAuthenticable;
import org.ocpteam.interfaces.IUserManagement;
import org.ocpteam.misc.LOG;
import org.ocpteam.misc.swt.QuickMessage;
import org.ocpteam.ui.swt.DataSourceWindow;


public class SignInWithAuthenticationWizard extends Wizard {

	public static void start(final DataSourceWindow window) {
		final Shell shell = new Shell(window.getShell().getDisplay());
		shell.setLayout(new FillLayout());

		SignInWithAuthenticationWizard wizard = new SignInWithAuthenticationWizard(window);
		WizardDialog dialog = new WizardDialog(shell, wizard) {
			@Override
			protected void finishPressed() {
				// TODO Auto-generated method stub
				super.finishPressed();
				window.tabFolder.setFocus();
			}
		};
		wizard.setWizardDialog(dialog);
		dialog.open();
		LOG.debug("about to dispose shell");
		shell.dispose();
	}


	private WizardDialog dialog;
	
	public void setWizardDialog(WizardDialog dialog) {
		this.dialog = dialog;
	}
	
	public WizardDialog getWizardDialog() {
		return dialog;
	}


	private SignInWithAuthenticationWizardPage p1;
	private DataSourceWindow window;

	public SignInWithAuthenticationWizard(DataSourceWindow window) {
		this.window = window;
		setWindowTitle("Sign In Wizard");
	}

	@Override
	public void addPages() {
		p1 = new SignInWithAuthenticationWizardPage(window);
		addPage(p1);
	}

	@Override
	public boolean performFinish() {
		LOG.debug("sign in user");
		try {
			IUserManagement um = window.ds.getComponent(IUserManagement.class);
			um.setUsername(p1.usernameText.getText());
			IAuthenticable auth = window.ds.getComponent(IAuthenticable.class);
			auth.setChallenge(p1.passwordText.getText());
			window.signIn();
		} catch (Exception e) {
			LOG.error(e);
			QuickMessage.error(window.getShell(), "Sign in failed: " + e.getMessage());
		}
		return true;
	}

	@Override
	public boolean canFinish() {
		return p1.isPageComplete();
	}
}
