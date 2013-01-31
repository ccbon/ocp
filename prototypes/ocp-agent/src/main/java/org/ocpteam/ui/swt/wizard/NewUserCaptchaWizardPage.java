package org.ocpteam.ui.swt.wizard;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.ocpteam.misc.LOG;
import org.ocpteam.ui.swt.DataSourceWindow;

public class NewUserCaptchaWizardPage extends WizardPage {
	public Text captchaAnswerText;

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
	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);

		Label lblProveYoureNot = new Label(container, SWT.NONE);
		lblProveYoureNot.setBounds(85, 39, 221, 13);
		lblProveYoureNot.setText("Prove you're not a robot");

		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setImage(new Image(parent.getDisplay(),
				DataSourceWindow.class
						.getResourceAsStream("captcha_fake.png")));
		lblNewLabel.setBounds(85, 58, 163, 69);

		Label lblTypeWhatYou = new Label(container, SWT.NONE);
		lblTypeWhatYou.setBounds(84, 133, 164, 13);
		lblTypeWhatYou.setText("Type what you see");

		captchaAnswerText = new Text(container, SWT.BORDER);
		captchaAnswerText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				getWizard().getContainer().updateButtons();
			}
		});
		captchaAnswerText.setBounds(81, 152, 167, 19);
		captchaAnswerText.setFocus();
	}

	@Override
	public IWizardPage getNextPage() {
		LOG.info("p2: getNextPage");
		return super.getNextPage();
	}

	@Override
	public boolean isPageComplete() {
		if (captchaAnswerText.getText().equals("")) {
			return false;
		}
		return super.isPageComplete();
	}

}
