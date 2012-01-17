package com.guenego.ftp.gui.install;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.guenego.misc.JLG;

public class FirstWizardPage extends WizardPage {
	protected boolean bIsPageComplete = true;
	protected Text serverHostnameText;

	/**
	 * Create the wizard.
	 */
	public FirstWizardPage() {
		super("firstPage");
		setTitle("Welcome to the OCP Agent Wizard!");
		setDescription("Please provide the information required.");
	}

	/**
	 * Create contents of the wizard.
	 * 
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		container.setTouchEnabled(true);

		setControl(container);

		Label lblAgentName = new Label(container, SWT.NONE);
		lblAgentName.setBounds(102, 54, 177, 13);
		lblAgentName.setText("FTP server hostname :");

		serverHostnameText = new Text(container, SWT.BORDER);
		serverHostnameText.setText("ftp.guenego.com");
		serverHostnameText.setBounds(102, 73, 177, 19);
	}

	@Override
	public boolean isPageComplete() {
		return bIsPageComplete;
	}
}
