package org.ocpteam.sftp.gui;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.ocpteam.misc.JLG;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;


public class SSHSignInWizardPage extends WizardPage {
	Text usernameText;
	Text passwordText;
	private Text text;
	private Text text_1;
	private Text text_2;

	/**
	 * Create the wizard.
	 */
	public SSHSignInWizardPage() {
		super("wizardPage");
		setTitle("Sign In Wizard");
		setDescription("Please enter the SSH connection information below");
	}

	/**
	 * Create contents of the wizard.
	 * 
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);

		Label lblUsername = new Label(container, SWT.NONE);
		lblUsername.setToolTipText("Enter your login + '@' + hostname");
		lblUsername.setBounds(10, 52, 187, 13);
		lblUsername.setText("Session (user@hostname)");

		usernameText = new Text(container, SWT.BORDER);
		usernameText.setText("jlouis@localhost");
		usernameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				getWizard().getContainer().updateButtons();
			}
		});

		usernameText.setBounds(10, 71, 187, 19);

		Label lblPassword = new Label(container, SWT.NONE);
		lblPassword.setBounds(10, 152, 49, 13);
		lblPassword.setText("Password");

		passwordText = new Text(container, SWT.BORDER | SWT.PASSWORD);
		passwordText.setText("jlouis");
		passwordText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				getWizard().getContainer().updateButtons();
			}
		});

		passwordText.setBounds(10, 171, 187, 19);
		
		Button btnPasswordAuthentication = new Button(container, SWT.RADIO);
		btnPasswordAuthentication.setSelection(true);
		btnPasswordAuthentication.setBounds(10, 96, 187, 16);
		btnPasswordAuthentication.setText("password authentication");
		
		Button btnRadioButton = new Button(container, SWT.RADIO);
		btnRadioButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		btnRadioButton.setBounds(10, 118, 187, 16);
		btnRadioButton.setText("private key authentication");
		
		Label lblPrivateKeyFile = new Label(container, SWT.NONE);
		lblPrivateKeyFile.setBounds(10, 196, 187, 13);
		lblPrivateKeyFile.setText("private key file");
		
		text = new Text(container, SWT.BORDER);
		text.setBounds(10, 215, 187, 19);
		
		Button btnChooseFile = new Button(container, SWT.NONE);
		btnChooseFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		btnChooseFile.setBounds(203, 213, 68, 23);
		btnChooseFile.setText("Choose File");
		
		Label lblPassphrase = new Label(container, SWT.NONE);
		lblPassphrase.setBounds(10, 240, 187, 13);
		lblPassphrase.setText("Passphrase");
		
		text_1 = new Text(container, SWT.BORDER);
		text_1.setBounds(10, 259, 187, 19);
		
		Label lblPort = new Label(container, SWT.NONE);
		lblPort.setBounds(228, 52, 49, 13);
		lblPort.setText("Port");
		
		text_2 = new Text(container, SWT.BORDER);
		text_2.setText("22");
		text_2.setBounds(228, 71, 76, 19);
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
