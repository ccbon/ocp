package org.ocpteam.ui.swt;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.ocpteam.layer.rsp.Authentication;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.swt.QuickMessage;
import org.ocpteam.protocol.ocp.OCPAgent;


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
			Authentication auth = wizard.window.ds.getAuthentication();
			auth.setLogin(wizard.getUsername());
			auth.setChallenge(wizard.getPassword());
			wizard.window.signIn(auth);
			wizard.bCanFinnish = true;
		} catch (Exception e) {
			throw QuickMessage.exception(getShell(), "Sorry. Cannot create the user.", e);
		}
	}

}
