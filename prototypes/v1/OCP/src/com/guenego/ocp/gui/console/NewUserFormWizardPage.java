package com.guenego.ocp.gui.console;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class NewUserFormWizardPage extends WizardPage {

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
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
	}

}
