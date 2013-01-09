package org.ocpteamx.protocol.dropbox.swt;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;

public class DROPBOXSignInWizardPage extends WizardPage {
	public Text text;
	public Button btnRememberMe;
	public boolean rememberMe;

	/**
	 * Create the wizard.
	 */
	public DROPBOXSignInWizardPage() {
		super("wizardPage");
		setTitle("Dropbox Sign in");
		setDescription("Please connect to your Dropbox account and click allow.");
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		
		Label lblUsername = new Label(container, SWT.NONE);
		lblUsername.setBounds(39, 54, 55, 15);
		lblUsername.setText("Username");
		
		text = new Text(container, SWT.BORDER);
		text.setBounds(39, 75, 202, 21);
		
		btnRememberMe = new Button(container, SWT.CHECK);
		btnRememberMe.setBounds(39, 102, 110, 16);
		btnRememberMe.setText("Remember me");
		btnRememberMe.setSelection(true);
	}
}
