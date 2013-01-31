package org.ocpteamx.protocol.sftp.swt;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.ocpteam.misc.LOG;

public class SSHSignInWizardPage extends WizardPage {
	Text sessionText;
	boolean bIsPassword = true;
	Group grpPassword;
	Group grpPrivateKey;
	Text passwordText;
	Label lblPrivateKeyFile;
	Text privateKeyFileText;
	Text passphraseText;
	private Text text_2;
	Text dirText;

	/**
	 * Create the wizard.
	 */
	public SSHSignInWizardPage() {
		super("wizardPage");
		LOG.debug_on();
		setTitle("Sign In Wizard");
		setDescription("Please enter the SSH connection information below");
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

		Label lblUsername = new Label(container, SWT.NONE);
		lblUsername.setToolTipText("Enter your login + '@' + hostname");
		lblUsername.setBounds(10, 10, 187, 13);
		lblUsername.setText("Session (user@hostname)");

		sessionText = new Text(container, SWT.BORDER);
		sessionText.setText("jlouis@localhost");
		sessionText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				getWizard().getContainer().updateButtons();
			}
		});

		sessionText.setBounds(10, 29, 187, 19);

		Button btnPasswordAuthentication = new Button(container, SWT.RADIO);
		btnPasswordAuthentication.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				LOG.info("bIsPassword = true");
				bIsPassword = true;
				update();
			}
		});
		btnPasswordAuthentication.setSelection(true);
		btnPasswordAuthentication.setBounds(10, 54, 187, 16);
		btnPasswordAuthentication.setText("password authentication");

		Button btnRadioButton = new Button(container, SWT.RADIO);
		btnRadioButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				LOG.info("bIsPassword = false");
				bIsPassword = false;
				update();
			}
		});
		btnRadioButton.setBounds(10, 76, 187, 16);
		btnRadioButton.setText("private key authentication");

		Label lblPort = new Label(container, SWT.NONE);
		lblPort.setBounds(238, 10, 49, 13);
		lblPort.setText("Port");

		text_2 = new Text(container, SWT.BORDER);
		text_2.setText("22");
		text_2.setBounds(238, 29, 76, 19);

		Label lblLocalDirectory = new Label(container, SWT.NONE);
		lblLocalDirectory.setBounds(10, 242, 187, 13);
		lblLocalDirectory.setText("Local Directory");

		dirText = new Text(container, SWT.BORDER);
		dirText.setBounds(10, 261, 261, 19);
		dirText.setText(System.getProperty("user.home"));

		Button btnChooseDirectory = new Button(container, SWT.NONE);
		btnChooseDirectory.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				DirectoryDialog directoryDialog = new DirectoryDialog(
						getShell());
				directoryDialog.setFilterPath(dirText.getText());
				directoryDialog
						.setMessage("Please select a directory and click OK");

				String dir = directoryDialog.open();
				if (dir != null) {
					dirText.setText(dir);
				}
			}
		});
		btnChooseDirectory.setBounds(277, 259, 95, 23);
		btnChooseDirectory.setText("Choose Directory");

		grpPassword = new Group(container, SWT.NONE);
		grpPassword.setText("password ");
		grpPassword.setBounds(10, 98, 214, 74);

		passwordText = new Text(grpPassword, SWT.BORDER | SWT.PASSWORD);
		passwordText.setLocation(10, 39);
		passwordText.setSize(187, 19);
		passwordText.setText("jlouis");

		Label lblPassword = new Label(grpPassword, SWT.NONE);
		lblPassword.setLocation(10, 21);
		lblPassword.setSize(49, 13);
		lblPassword.setText("Password");

		grpPrivateKey = new Group(container, SWT.NONE);
		grpPrivateKey.setText("private key");
		grpPrivateKey.setBounds(238, 54, 310, 118);

		lblPrivateKeyFile = new Label(grpPrivateKey, SWT.NONE);
		lblPrivateKeyFile.setLocation(10, 20);
		lblPrivateKeyFile.setSize(187, 13);
		lblPrivateKeyFile.setText("private key file");

		privateKeyFileText = new Text(grpPrivateKey, SWT.BORDER);
		privateKeyFileText.setText("C:\\cygwin\\home\\jlouis\\.ssh\\id_rsa");
		privateKeyFileText.setLocation(10, 36);
		privateKeyFileText.setSize(187, 19);

		Button btnChooseFile = new Button(grpPrivateKey, SWT.NONE);
		btnChooseFile.setBounds(219, 34, 68, 23);
		btnChooseFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fileDialog = new FileDialog(
						getShell());
				fileDialog.setFilterPath(dirText.getText());
				fileDialog.setText("Please select a private key file.");
				String fileString = fileDialog.open();
				if (fileString != null) {
					privateKeyFileText.setText(fileString);
				}
			}
		});
		btnChooseFile.setText("Choose File");

		Label lblPassphrase = new Label(grpPrivateKey, SWT.NONE);
		lblPassphrase.setBounds(10, 63, 187, 13);
		lblPassphrase.setText("Passphrase");

		passphraseText = new Text(grpPrivateKey, SWT.BORDER | SWT.PASSWORD);
		passphraseText.setBounds(10, 82, 187, 19);
		passwordText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				getWizard().getContainer().updateButtons();
			}
		});

		update();
	}

	protected void update() {
		for (Control c : grpPassword.getChildren()) {
			c.setEnabled(bIsPassword);
		}
		for (Control c : grpPrivateKey.getChildren()) {
			c.setEnabled(!bIsPassword);
		}
	}

	@Override
	public boolean isPageComplete() {
		LOG.info("isPageComplete");
		if (sessionText.getText().equals("")) {
			return false;
		}
		if (passwordText.getText().equals("")) {
			return false;
		}

		return super.isPageComplete();
	}
}
