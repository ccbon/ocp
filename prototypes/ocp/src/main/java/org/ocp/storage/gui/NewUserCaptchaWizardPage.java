package org.ocp.storage.gui;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;

import org.ocp.misc.JLG;
import org.ocp.ocp.OCPAgent;
import org.ocp.storage.User;

public class NewUserCaptchaWizardPage extends WizardPage {
	private Text captchaAnswerText;

	/**
	 * Create the wizard.
	 */
	public NewUserCaptchaWizardPage() {
		super("NewUserCaptchaWizardPage");
		setTitle("Wizard Page title");
		setDescription("Wizard Page description");
	}

	/**
	 * Create contents of the wizard.
	 * 
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);

		Label lblProveYoureNot = new Label(container, SWT.NONE);
		lblProveYoureNot.setBounds(85, 39, 221, 13);
		lblProveYoureNot.setText("Prove you're not a robot");

		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setImage(new Image(parent.getDisplay(),
				NewUserFormWizardPage.class
						.getResourceAsStream("captcha_fake.png")));
		lblNewLabel.setBounds(85, 58, 163, 69);

		Label lblTypeWhatYou = new Label(container, SWT.NONE);
		lblTypeWhatYou.setBounds(84, 133, 164, 13);
		lblTypeWhatYou.setText("Type what you see");

		captchaAnswerText = new Text(container, SWT.BORDER);
		captchaAnswerText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				getWizard().getContainer().updateButtons();
			}
		});
		captchaAnswerText.setBounds(81, 152, 167, 19);
		captchaAnswerText.setFocus();
	}

	@Override
	public IWizardPage getNextPage() {
		JLG.debug("p2: getNextPage");
		return super.getNextPage();
	}

	@Override
	public boolean canFlipToNextPage() {
		if (captchaAnswerText.getText().equals("")) {
			return false;
		}
		return super.canFlipToNextPage();
	}

	public void onNextPage() throws Exception {
		try {
			JLG.debug("creating the user");
			NewUserWizard wizard = (NewUserWizard) getWizard();
			OCPAgent agent = (OCPAgent) wizard.getAgent();
			agent.createUser(wizard.getUsername(), wizard.getPassword(), 2,
					wizard.getCaptcha(), captchaAnswerText.getText());
			User user = agent.login(wizard.getUsername(), wizard.getPassword());
			wizard.getAdminConsole().setUser(user);
			wizard.getAdminConsole().addUserSyncTab();
			wizard.getAdminConsole().addUserExplorerTab();
			wizard.bCanFinnish = true;
		} catch (Exception e) {
			MessageBox messageBox = new MessageBox(getShell(), SWT.ICON_ERROR
					| SWT.OK);
			messageBox.setMessage("Sorry. Cannot create the user.");
			messageBox.setText("Error !");
			messageBox.open();
			throw e;
		}
	}

}
