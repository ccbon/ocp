package org.ocpteam.ui.swt;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.ocpteam.misc.JLG;


public class NewUserSucessWizardPage extends WizardPage {

	/**
	 * Create the wizard.
	 */
	public NewUserSucessWizardPage() {
		super("wizardPage");
		setTitle("Congratulations !");
		setDescription("User has been created.");
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		JLG.debug("p3: creating control");
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
	}
	
}
