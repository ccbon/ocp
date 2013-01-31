package org.ocpteam.ui.swt.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.ocpteam.component.DataSource;
import org.ocpteam.interfaces.ICaptcha;
import org.ocpteam.interfaces.IUserCreation;
import org.ocpteam.misc.LOG;

public class NewUserFormWizardPage extends WizardPage {
	Text usernameText;
	Text passwordText;

	/**
	 * Create the wizard.
	 */
	public NewUserFormWizardPage() {
		super("NewUserFormWizardPage");
		setTitle("Create new user");
		setDescription("Please fill this form.");
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

		Label lblUsername = new Label(container, SWT.NONE);
		lblUsername.setBounds(57, 27, 49, 13);
		lblUsername.setText("Username");

		usernameText = new Text(container, SWT.BORDER);
		usernameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				getWizard().getContainer().updateButtons();
			}
		});
		usernameText.setBounds(57, 46, 153, 19);

		Label lblPassword = new Label(container, SWT.NONE);
		lblPassword.setBounds(57, 87, 49, 13);
		lblPassword.setText("Password");

		passwordText = new Text(container, SWT.BORDER | SWT.PASSWORD);
		passwordText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				getWizard().getContainer().updateButtons();
			}
		});
		passwordText.setBounds(57, 106, 153, 19);
		container.setTabList(new Control[] { usernameText, passwordText });
		usernameText.setFocus();
	}

	
	@Override
	public boolean isPageComplete() {
		LOG.info("isPageComplete");
		if (usernameText.getText().equals("")) {
			return false;
		}
		if (passwordText.getText().equals("")) {
			return false;
		}

		return super.isPageComplete();
	}
	
	public void onNextPage() throws Exception {
		LOG.info("onNextPage");
		NewUserWizard wizard = (NewUserWizard) getWizard();
		DataSource ds = wizard.window.ds;
		IUserCreation uc = ds.getComponent(IUserCreation.class);
		uc.setUser(usernameText.getText());
		uc.setPassword(passwordText.getText());
		if (uc.needsCaptcha()) {
			ICaptcha c = uc.getCaptcha();
			wizard.setCaptcha(c);
		}

	}

}
