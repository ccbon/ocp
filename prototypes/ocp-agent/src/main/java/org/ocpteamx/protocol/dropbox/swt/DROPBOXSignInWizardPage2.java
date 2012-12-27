package org.ocpteamx.protocol.dropbox.swt;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class DROPBOXSignInWizardPage2 extends WizardPage {

	/**
	 * Create the wizard.
	 */
	public DROPBOXSignInWizardPage2() {
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
	}
}
