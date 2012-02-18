package org.ocpteam.ftp.swt;

import java.io.File;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class FirstWizardPage extends WizardPage {
	protected boolean bIsPageComplete = true;
	protected Text serverHostnameText;
	protected Text defaultLocalDirText;
	protected Text portText;

	/**
	 * Create the wizard.
	 */
	public FirstWizardPage() {
		super("firstPage");
		setTitle("Welcome to the FTP Agent setup wizard!");
		setDescription("Please provide the information required.");
	}

	/**
	 * Create contents of the wizard.
	 * 
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		//container.setTouchEnabled(true);

		setControl(container);

		Label lblAgentName = new Label(container, SWT.NONE);
		lblAgentName.setBounds(102, 54, 177, 13);
		lblAgentName.setText("FTP server hostname :");

		serverHostnameText = new Text(container, SWT.BORDER);
		serverHostnameText.setText("127.0.0.1");
		serverHostnameText.setBounds(102, 73, 177, 19);
		
		Label lblDefaultLocalDirectory = new Label(container, SWT.NONE);
		lblDefaultLocalDirectory.setBounds(102, 162, 177, 13);
		lblDefaultLocalDirectory.setText("Default Local directory :");
		
		defaultLocalDirText = new Text(container, SWT.BORDER);
		defaultLocalDirText.setText(System.getProperty("user.home") + File.separator + "ftp" + File.separator + "local");
		defaultLocalDirText.setBounds(102, 181, 177, 19);
		
		Label lblPort = new Label(container, SWT.NONE);
		lblPort.setBounds(102, 98, 177, 13);
		lblPort.setText("Port :");
		
		portText = new Text(container, SWT.BORDER);
		portText.setText("21");
		portText.setBounds(102, 117, 76, 19);
		
		Button btnBrowse = new Button(container, SWT.NONE);
		btnBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog directoryDialog = new DirectoryDialog(
						getShell());
				directoryDialog.setFilterPath(defaultLocalDirText.getText());
				directoryDialog
						.setMessage("Please select a directory and click OK");

				String dir = directoryDialog.open();
				if (dir != null) {
					defaultLocalDirText.setText(dir);
				}

			}
		});
		btnBrowse.setBounds(285, 179, 68, 23);
		btnBrowse.setText("Browse");
	}

	@Override
	public boolean isPageComplete() {
		return bIsPageComplete;
	}
}
