package org.ocpteam.ui.swt;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.ocpteam.interfaces.IAuthenticable;
import org.ocpteam.interfaces.ICaptcha;
import org.ocpteam.interfaces.IUser;
import org.ocpteam.interfaces.IUserCreation;
import org.ocpteam.interfaces.IUserManagement;
import org.ocpteam.misc.LOG;
import org.ocpteam.misc.swt.QuickMessage;

public class NewUserWizard extends Wizard {

	public static void start(final DataSourceWindow window) {
		Display display = window.getShell().getDisplay();
		final Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());

		WizardDialog dialog = new WizardDialog(shell, new NewUserWizard(window)) {
			@Override
			protected void nextPressed() {
				try {
					LOG.debug("next button pressed");
					IWizardPage page = this.getCurrentPage();
					if (page.getClass() == NewUserFormWizardPage.class) {
						LOG.debug("form page");
						((NewUserFormWizardPage) page).onNextPage();
					}
					super.nextPressed();
				} catch (Exception e) {
					LOG.error(e);
					this.close();
				}

				getContents().setFocus();
			}

			@Override
			protected void finishPressed() {
				super.finishPressed();
				window.tabFolder.setFocus();
			}
		};
		dialog.open();
		LOG.debug("about to dispose shell");
	}

	private NewUserFormWizardPage p1;
	private NewUserCaptchaWizardPage p2;
	private IUser user;
	private ICaptcha captcha;
	public DataSourceWindow window;

	public NewUserWizard(DataSourceWindow window) {
		this.window = window;
		setWindowTitle("Create new user Wizard");
	}

	@Override
	public void addPages() {
		p1 = new NewUserFormWizardPage();
		addPage(p1);
		IUserCreation uc = window.ds.getComponent(IUserCreation.class);
		if (uc.needsCaptcha()) {
			p2 = new NewUserCaptchaWizardPage();
			addPage(p2);
		}
	}

	@Override
	public boolean canFinish() {
		IWizardPage[] pages = getPages();
		for (int i = 0; i < pages.length; i++) {
			if (!pages[i].isPageComplete()) {
				LOG.debug("page not completed: " + i);
				return false;
			}
		}
		LOG.debug("all pages completed.");
		return true;
	}

	@Override
	public boolean performFinish() {
		try {
			LOG.debug("creating the user");
			IUserCreation uc = window.ds.getComponent(IUserCreation.class);
			if (uc.needsCaptcha()) {
				uc.setCaptcha(getCaptcha());
				uc.setAnswer(p2.captchaAnswerText.getText());
			} else {
				uc.setUser(p1.usernameText.getText());
				uc.setPassword(p1.passwordText.getText());
			}
			uc.createUser();
			if (window.ds.usesComponent(IUserManagement.class)) {
				IUserManagement ui = window.ds
						.getComponent(IUserManagement.class);
				ui.setUsername(uc.getUser().getUsername());
				IAuthenticable auth = window.ds.getComponent(IAuthenticable.class);
				auth.setChallenge(uc.getPassword());
				window.signIn();
			}
		} catch (Exception e) {
			e.printStackTrace();
			QuickMessage.error(getShell(), "Sorry. Cannot create the user. Error message: "
					+ e.getMessage());
		}
		return true;
	}

	public void setCaptcha(ICaptcha c) {
		this.captcha = c;
	}

	public ICaptcha getCaptcha() {
		return captcha;
	}

	public IUser getUser() {
		return user;
	}

	public void setUser(IUser user) {
		this.user = user;
	}

}
