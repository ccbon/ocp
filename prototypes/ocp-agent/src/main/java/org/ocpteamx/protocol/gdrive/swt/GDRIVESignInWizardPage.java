package org.ocpteamx.protocol.gdrive.swt;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class GDRIVESignInWizardPage extends WizardPage {
	public Text codeText;

	/**
	 * Create the wizard.
	 */
	public GDRIVESignInWizardPage() {
		super("wizardPage");
		setTitle("Google Drive Sign In");
		setDescription("Copy/paste the given oAuth code.");
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		
		Label lblCode = new Label(container, SWT.NONE);
		lblCode.setBounds(10, 27, 55, 15);
		lblCode.setText("Code :");
		
		codeText = new Text(container, SWT.BORDER);
		codeText.setBounds(10, 55, 296, 21);
	}
}
