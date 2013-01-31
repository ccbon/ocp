package org.ocpteam.ui.swt.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.ocpteam.interfaces.IUserCreation;
import org.ocpteam.misc.LOG;
import org.ocpteam.ui.swt.DataSourceWindow;

public class SignInWithAuthenticationWizardPage extends WizardPage {
	Text usernameText;
	Text passwordText;
	private DataSourceWindow window;
	private Button btnCreateAnNew;

	/**
	 * Create the wizard.
	 * 
	 * @param window
	 */
	public SignInWithAuthenticationWizardPage(DataSourceWindow window) {
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
		} catch (Exception e) {
		}

		Label lblUsername = new Label(container, SWT.NONE);
		lblUsername.setBounds(10, 52, 75, 13);
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
		usernameText.setFocus();

		Label lblPassword = new Label(container, SWT.NONE);
		lblPassword.setBounds(10, 96, 75, 13);
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

		if (window.ds.usesComponent(IUserCreation.class)) {
			btnCreateAnNew = new Button(container, SWT.NONE);
			btnCreateAnNew.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					LOG.info("Create an account");
					SignInWithAuthenticationWizard wizard = (SignInWithAuthenticationWizard) getWizard();
					wizard.getWizardDialog().close();
					window.newUserAction.run();
					LOG.info("Create an account 2");
				}
			});
			btnCreateAnNew.setBounds(10, 10, 152, 25);
			btnCreateAnNew.setText("Create a new account");
		}

		Control[] list = new Control[] { btnCreateAnNew, usernameText,
				passwordText };
		container.setTabList(list);
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
}
