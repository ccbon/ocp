package org.ocpteamx.protocol.zip.swt;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class ZipConnectWizardPage extends WizardPage {
	protected boolean bIsPageComplete = true;
	protected Text fileText;

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
	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		// container.setTouchEnabled(true);

		setControl(container);

		Label lblAgentName = new Label(container, SWT.NONE);
		lblAgentName.setBounds(102, 54, 177, 13);
		lblAgentName.setText("Please enter the ZIP filename :");

		fileText = new Text(container, SWT.BORDER);
		fileText.setText("C:\\Documents and Settings\\jlouis\\My Documents\\Downloads\\apache_net.zip");
		fileText.setBounds(102, 75, 289, 19);

		Button btnBrowse = new Button(container, SWT.NONE);
		btnBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				FileDialog fileDialog = new FileDialog(getShell());
				fileDialog.setFilterPath(System.getProperty("user.home"));
				fileDialog.setText("Please select a zip file and click OK");

				String filename = fileDialog.open();
				if (filename != null) {
					fileText.setText(filename);
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
