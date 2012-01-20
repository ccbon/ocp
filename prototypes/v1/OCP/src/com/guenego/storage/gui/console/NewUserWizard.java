package com.guenego.storage.gui.console;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.guenego.misc.JLG;
import com.guenego.ocp.Captcha;
import com.guenego.storage.Agent;

public class NewUserWizard extends Wizard {

	public static void start(Display display, Agent agent, final AdminConsole window) {
		
		final Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());

		WizardDialog dialog = new WizardDialog(shell, new NewUserWizard(agent, window)) {
			@Override
			protected void nextPressed() {
				try {
					JLG.debug("next button pressed");
					IWizardPage page = this.getCurrentPage();
					if (page.getClass() == NewUserFormWizardPage.class) {
						JLG.debug("form page");
						((NewUserFormWizardPage) page).onNextPage();
					} else if (page.getClass() == NewUserCaptchaWizardPage.class) {
						JLG.debug("captcha page");
						((NewUserCaptchaWizardPage) page).onNextPage();
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
				// TODO Auto-generated method stub
				super.finishPressed();
				window.tabFolder.setFocus();
			}
		};
		dialog.open();
		JLG.debug("about to dispose shell");
		// shell.dispose();
	}

	private NewUserFormWizardPage p1;
	private NewUserCaptchaWizardPage p2;
	private NewUserSucessWizardPage p3;
	private Agent agent;
	private Captcha captcha;
	private String username;
	private String password;
	public boolean bCanFinnish;
	private AdminConsole window;

	public NewUserWizard(Agent agent, AdminConsole window) {
		this.agent = agent;
		this.window = window;
		setWindowTitle("Create new user Wizard");
		bCanFinnish = false;
	}

	@Override
	public void addPages() {
		p1 = new NewUserFormWizardPage();
		addPage(p1);
		p2 = new NewUserCaptchaWizardPage();
		addPage(p2);
		p3 = new NewUserSucessWizardPage();
		addPage(p3);
	}

	@Override
	public boolean performFinish() {
		return true;
	}

	public boolean canFinish() {
		return bCanFinnish;
	}

	public Agent getAgent() {
		return agent;
	}

	public void setCaptcha(Captcha c) {
		this.captcha = c;
	}

	public void setUsername(String text) {
		this.username = text;
	}

	public void setPassword(String text) {
		this.password = text;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public Captcha getCaptcha() {
		return captcha;
	}

	public AdminConsole getAdminConsole() {
		return window;
	}

}
