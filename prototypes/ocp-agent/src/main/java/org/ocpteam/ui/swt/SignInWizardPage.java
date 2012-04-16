package org.ocpteam.ui.swt;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.ocpteam.misc.JLG;


public class SignInWizardPage extends WizardPage {
	Text usernameText;
	Text passwordText;
	private DataSourceWindow window;

	/**
	 * Create the wizard.
	 * @param window 
	 */
	public SignInWizardPage(DataSourceWindow window) {
		super("wizardPage");
		this.window = window;
		setTitle("Sign In Wizard");
		setDescription("Please enter your username/password and click on 'Next' to open a session.");
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

		String username = "";
		String password = "";
		try {
			String[] array = window.ds.getURI().getUserInfo().split(":");
			username = array[0];
			password = array[1];
		}  catch (Exception e) {
		}

		Label lblUsername = new Label(container, SWT.NONE);
		lblUsername.setBounds(10, 52, 49, 13);
		lblUsername.setText("Username");

		usernameText = new Text(container, SWT.BORDER);
		usernameText.setText(username);
		usernameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				getWizard().getContainer().updateButtons();
			}
		});

		usernameText.setBounds(10, 71, 187, 19);

		Label lblPassword = new Label(container, SWT.NONE);
		lblPassword.setBounds(10, 96, 49, 13);
		lblPassword.setText("Password");

		passwordText = new Text(container, SWT.BORDER | SWT.PASSWORD);
		passwordText.setText(password);
		passwordText.addModifyListener(new ModifyListener() {
			@Override
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
