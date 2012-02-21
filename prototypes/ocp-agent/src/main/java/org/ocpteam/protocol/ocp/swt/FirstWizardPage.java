package org.ocpteam.protocol.ocp.swt;

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
import org.ocpteam.misc.JLG;
import org.ocpteam.protocol.ocp.OCPAgent;


public class FirstWizardPage extends WizardPage {
	protected boolean bIsPageComplete = true;
	protected Text agentNameText;
	public Text listenerPortText;
	public Text sponsorText;
	private Label sponsorLabel;
	public Text sponsorPublicServerText;
	Button btnCheckButton;

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
		final ConfigWizard wizard = (ConfigWizard) getWizard();
		Composite container = new Composite(parent, SWT.NULL);
		//container.setTouchEnabled(true);

		setControl(container);

		Label lblAgentName = new Label(container, SWT.NONE);
		lblAgentName.setBounds(102, 54, 98, 13);
		lblAgentName.setText("Agent name :");

		agentNameText = new Text(container, SWT.BORDER);
		agentNameText.setText("anonymous");
		agentNameText.setBounds(102, 73, 177, 19);

		Label lblListeningPort = new Label(container, SWT.NONE);
		lblListeningPort.setBounds(102, 10, 177, 13);
		lblListeningPort.setText("Listening port :");

		listenerPortText = new Text(container, SWT.BORDER);
		listenerPortText.setText(wizard.listenerPort);
		listenerPortText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				bIsPageComplete = JLG.isInteger(listenerPortText.getText());
				wizard.getContainer().updateButtons();
			}
		});

		listenerPortText.setBounds(102, 29, 76, 19);

		Button joinOCPButton = new Button(container, SWT.RADIO);
		joinOCPButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				wizard.bIsFirstAgent = false;
				sponsorLabel.setEnabled(true);
				sponsorText.setEnabled(true);
				wizard.getContainer().updateButtons();
			}
		});
		joinOCPButton.setBounds(102, 133, 177, 16);
		joinOCPButton.setText("Join an existing OCP network");

		Button createOCPButton = new Button(container, SWT.RADIO);
		createOCPButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				wizard.bIsFirstAgent = true;
				sponsorLabel.setEnabled(false);
				sponsorText.setEnabled(false);
				wizard.getContainer().updateButtons();
			}
		});
		createOCPButton.setBounds(102, 155, 177, 16);
		createOCPButton.setText("Create a new OCP network");

		sponsorLabel = new Label(container, SWT.NONE);
		sponsorLabel.setBounds(102, 177, 177, 13);
		sponsorLabel.setText("Sponsor URL :");
		sponsorLabel.setEnabled(false);

		sponsorText = new Text(container, SWT.BORDER);
		sponsorText.setText("tcp://localhost:22221");
		sponsorText.setBounds(102, 196, 177, 19);
		sponsorText.setEnabled(false);
		
		btnCheckButton = new Button(container, SWT.CHECK);
		btnCheckButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				sponsorPublicServerText.setEnabled(btnCheckButton.getSelection());
				wizard.getContainer().updateButtons();
			}
		});
		btnCheckButton.setSelection(true);
		btnCheckButton.setBounds(102, 221, 177, 16);
		btnCheckButton.setText("Use a public sponsor server");
		
		sponsorPublicServerText = new Text(container, SWT.BORDER);
		sponsorPublicServerText.setText(OCPAgent.DEFAULT_SPONSOR_SERVER_URL);
		sponsorPublicServerText.setBounds(102, 241, 177, 19);
	}

	@Override
	public boolean isPageComplete() {
		return bIsPageComplete;
	}
}
