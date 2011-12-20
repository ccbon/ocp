package com.guenego.ocp.gui.install;

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

public class FirstWizardPage extends WizardPage {
	protected Text listenerURLText;
	protected boolean bIsPageComplete = true;
	public Button onlyClientButton;

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

		onlyClientButton = new Button(container, SWT.CHECK);
		onlyClientButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (onlyClientButton.getSelection()) {
					((SecondWizardPage) getWizard().getPage("secondPage")).joinButton.setEnabled(false);
					((SecondWizardPage) getWizard().getPage("secondPage")).joinButton.setSelection(true);
					((SecondWizardPage) getWizard().getPage("secondPage")).sponsorURLText.setEnabled(true);
					listenerURLText.setEnabled(false);
				} else {
					((SecondWizardPage) getWizard().getPage("secondPage")).joinButton.setEnabled(true);
					listenerURLText.setEnabled(true);
				}
			}
		});
		onlyClientButton.setBounds(102, 29, 292, 16);
		onlyClientButton.setText("Make this agent only a client.");
		onlyClientButton.setSelection(false);

		listenerURLText = new Text(container, SWT.BORDER);
		listenerURLText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				if (listenerURLText.getText().equals("")) {
					bIsPageComplete = false;
					getWizard().getContainer().updateButtons();
				} else if (bIsPageComplete == false) {
					bIsPageComplete = true;
					getWizard().getContainer().updateButtons();
					
				}
			}
		});
		listenerURLText.setText("tcp://localhost:22221");
		listenerURLText.setBounds(104, 89, 175, 19);

		Label lblSponsorUrl = new Label(container, SWT.NONE);
		lblSponsorUrl.setBounds(104, 70, 179, 13);
		lblSponsorUrl.setText("Listener URL :");
	}

	@Override
	public boolean isPageComplete() {
		return bIsPageComplete;
	}
}
