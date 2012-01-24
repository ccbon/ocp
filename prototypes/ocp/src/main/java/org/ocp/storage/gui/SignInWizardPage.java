package org.ocp.storage.gui;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import org.ocp.misc.JLG;

public class SignInWizardPage extends WizardPage {
	Text usernameText;
	Text passwordText;

	/**
	 * Create the wizard.
	 */
	public SignInWizardPage() {
		super("wizardPage");
		setTitle("Sign In Wizard");
		setDescription("Please enter your username/password and click on 'Next' to open a session.");
	}

	/**
	 * Create contents of the wizard.
	 * 
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);

		Label lblUsername = new Label(container, SWT.NONE);
		lblUsername.setBounds(10, 52, 49, 13);
		lblUsername.setText("Username");

		usernameText = new Text(container, SWT.BORDER);
		usernameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				getWizard().getContainer().updateButtons();
			}
		});

		usernameText.setBounds(10, 71, 187, 19);

		Label lblPassword = new Label(container, SWT.NONE);
		lblPassword.setBounds(10, 96, 49, 13);
		lblPassword.setText("Password");

		passwordText = new Text(container, SWT.BORDER | SWT.PASSWORD);
		passwordText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				getWizard().getContainer().updateButtons();
			}
		});

		passwordText.setBounds(10, 115, 187, 19);
	}

	@Override
	public boolean isPageComplete() {
		JLG.debug("isPageComplete");
		if (usernameText.getText().equals("")) {
			return false;
		}
		if (passwordText.getText().equals("")) {
			return false;
		}

		return super.isPageComplete();
	}

}
