package org.ocpteam.ui.swt;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.ocpteam.misc.LOG;


public class SignInWizardPage extends WizardPage {
	Text usernameText;
	private DataSourceWindow window;

	/**
	 * Create the wizard.
	 * @param window 
	 */
	public SignInWizardPage(DataSourceWindow window) {
		super("wizardPage");
		this.window = window;
		setTitle("Sign In Wizard");
		setDescription("Please enter your username and click on 'Next' to open a session.");
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
		try {
			String[] array = window.ds.getURI().getUserInfo().split(":");
			username = array[0];
		}  catch (Exception e) {
		}

		Label lblUsername = new Label(container, SWT.NONE);
		lblUsername.setBounds(10, 52, 85, 13);
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
	}

	@Override
	public boolean isPageComplete() {
		LOG.debug("isPageComplete");
		if (usernameText.getText().equals("")) {
			return false;
		}
		return super.isPageComplete();
	}

}
