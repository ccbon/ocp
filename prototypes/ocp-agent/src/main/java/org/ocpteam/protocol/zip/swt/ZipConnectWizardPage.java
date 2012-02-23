package org.ocpteam.protocol.zip.swt;

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

public class ZipConnectWizardPage extends WizardPage {
	protected boolean bIsPageComplete = true;
	protected Text defaultLocalDirText;

	/**
	 * Create the wizard.
	 */
	public ZipConnectWizardPage() {
		super("firstPage");
		setTitle("Welcome to the ZIP Agent connection wizard!");
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
		lblAgentName.setText("Please enter the ZIP filename :");
		
		defaultLocalDirText = new Text(container, SWT.BORDER);
		defaultLocalDirText.setText(System.getProperty("user.home") + File.separator + "ftp" + File.separator + "local");
		defaultLocalDirText.setBounds(102, 75, 289, 19);
		
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
		btnBrowse.setBounds(102, 100, 68, 23);
		btnBrowse.setText("Browse");
	}

	@Override
	public boolean isPageComplete() {
		return bIsPageComplete;
	}
}
