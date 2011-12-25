package com.guenego.ocp.gui.console;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.guenego.misc.JLG;
import com.guenego.ocp.Agent;
import com.guenego.ocp.User;

public class NewUserCaptchaWizardPage extends WizardPage {
	private Text text;

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

		text = new Text(container, SWT.BORDER);
		text.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				getWizard().getContainer().updateButtons();
			}
		});
		text.setBounds(81, 152, 167, 19);
	}

	@Override
	public IWizardPage getNextPage() {
		JLG.debug("p2: getNextPage");
		return super.getNextPage();
	}

	@Override
	public boolean canFlipToNextPage() {
		if (text.getText().equals("")) {
			return false;
		}
		return super.canFlipToNextPage();
	}

	public void onNextPage() throws Exception {
		JLG.debug("creating the user");
		NewUserWizard wizard = (NewUserWizard) getWizard();
		Agent agent = wizard.getAgent();
		agent.createUser(wizard.getUsername(), wizard.getPassword(), 2,
				wizard.getCaptcha(), text.getText());
		User user = agent.login(wizard.getUsername(), wizard.getPassword());
		wizard.getAdminConsole().addUserTab(user);
		wizard.bCanFinnish = true;
	}

}
