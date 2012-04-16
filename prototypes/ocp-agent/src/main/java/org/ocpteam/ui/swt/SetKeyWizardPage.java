package org.ocpteam.ui.swt;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class SetKeyWizardPage extends WizardPage {
	public Text keyText;
	public Text valueText;

	/**
	 * Create the wizard.
	 */
	public SetKeyWizardPage() {
		super("wizardPage");
		setTitle("Set Key Wizard");
		setDescription("Please fill the requested form.");
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		
		Label lblKey = new Label(container, SWT.NONE);
		lblKey.setBounds(10, 55, 49, 13);
		lblKey.setText("Key");
		
		keyText = new Text(container, SWT.BORDER);
		keyText.setBounds(10, 74, 184, 19);
		
		Label lblValue = new Label(container, SWT.NONE);
		lblValue.setBounds(10, 119, 49, 13);
		lblValue.setText("Value");
		
		valueText = new Text(container, SWT.BORDER);
		valueText.setBounds(10, 138, 184, 19);
	}
}
