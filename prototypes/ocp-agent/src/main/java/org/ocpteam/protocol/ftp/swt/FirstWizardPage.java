package org.ocpteam.protocol.ftp.swt;

import java.net.URI;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.ocpteam.protocol.ftp.FTPDataSource;

public class FirstWizardPage extends WizardPage {

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
	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		//container.setTouchEnabled(true);

		setControl(container);

		Label lblAgentName = new Label(container, SWT.NONE);
		lblAgentName.setBounds(102, 54, 177, 13);
		lblAgentName.setText("FTP server hostname :");

		serverHostnameText = new Text(container, SWT.BORDER);
		serverHostnameText.setText("127.0.0.1");
		serverHostnameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				refresh();
			}
		});
		serverHostnameText.setBounds(102, 73, 177, 19);
		
		Label lblDefaultLocalDirectory = new Label(container, SWT.NONE);
		lblDefaultLocalDirectory.setBounds(102, 162, 177, 13);
		lblDefaultLocalDirectory.setText("Default Local directory :");
		
		defaultLocalDirText = new Text(container, SWT.BORDER);
		defaultLocalDirText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				refresh();
			}
		});
		defaultLocalDirText.setText("/");
		defaultLocalDirText.setBounds(102, 181, 177, 19);
		
		Label lblPort = new Label(container, SWT.NONE);
		lblPort.setBounds(102, 98, 177, 13);
		lblPort.setText("Port :");
		
		portText = new Text(container, SWT.BORDER);
		portText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				refresh();
			}
		});
		portText.setText("21");
		portText.setBounds(102, 117, 76, 19);
	}

	protected void refresh() {
		try {
			String hostname = "";
			if (serverHostnameText != null) {
				hostname = serverHostnameText.getText();
			}
			String port = "";
			if (portText != null) {
				port = portText.getText();
			}
			String dir = "";
			if (defaultLocalDirText != null) {
				dir = defaultLocalDirText.getText();
			}
			URI uri = new URI("ftp://" + hostname + ":" + port + dir);
			new FTPDataSource(uri);
		} catch (Exception e) {
			e.printStackTrace();
			setErrorMessage("bad input");
		}
		getWizard().getContainer().updateButtons();
	}

	@Override
	public boolean isPageComplete() {
		if (getErrorMessage() != null) {
			return false;
		}
		return true;
	}
}
