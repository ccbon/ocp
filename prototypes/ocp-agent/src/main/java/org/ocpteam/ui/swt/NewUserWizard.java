package org.ocpteam.ui.swt;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.ocpteam.component.Authentication;
import org.ocpteam.component.UserIdentification;
import org.ocpteam.interfaces.ICaptcha;
import org.ocpteam.interfaces.IUser;
import org.ocpteam.interfaces.IUserCreation;
import org.ocpteam.misc.JLG;
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
					JLG.debug("next button pressed");
					IWizardPage page = this.getCurrentPage();
					if (page.getClass() == NewUserFormWizardPage.class) {
						JLG.debug("form page");
						((NewUserFormWizardPage) page).onNextPage();
					}
					super.nextPressed();
				} catch (Exception e) {
					JLG.error(e);
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
		JLG.debug("about to dispose shell");
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
				JLG.debug("page not completed: " + i);
				return false;
			}
		}
		JLG.debug("all pages completed.");
		return true;
	}

	@Override
	public boolean performFinish() {
		try {
			JLG.debug("creating the user");
			IUserCreation uc = window.ds
					.getComponent(IUserCreation.class);
			if (uc.needsCaptcha()) {
				uc.setCaptcha(getCaptcha());
				uc.setAnswer(p2.captchaAnswerText.getText());
			}
			uc.createUser();
			if (window.ds.usesComponent(UserIdentification.class)) {
				UserIdentification ui = window.ds
						.getComponent(UserIdentification.class);
				if (ui instanceof Authentication) {
					Authentication auth = (Authentication) ui;
					auth.setUsername(uc.getUser().getUsername());
					auth.setChallenge(uc.getPassword());
				}
				window.signIn();
			}
		} catch (Exception e) {
			e.printStackTrace();
			QuickMessage.error(getShell(),
					"Sorry. Cannot create the user. e=" + e.getMessage());
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
