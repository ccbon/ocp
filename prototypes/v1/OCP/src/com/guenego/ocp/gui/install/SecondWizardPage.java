package com.guenego.ocp.gui.install;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;

public class SecondWizardPage extends WizardPage {
	public Text sponsorURLText;
	private boolean bIsPageComplete = true;
	public Button joinButton;

	/**
	 * Create the wizard.
	 */
	public SecondWizardPage() {
		super("secondPage");
		setTitle("Second Wizard Page title");
		setDescription("Second Wizard Page description");
	}

	/**
	 * Create contents of the wizard.
	 * 
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);

		joinButton = new Button(container, SWT.CHECK);
		
		joinButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println("check box event.");
				NetworkWizardPage networkPage = (NetworkWizardPage) getWizard().getPage("networkPage");
				if (joinButton.getSelection()) {
					sponsorURLText.setEnabled(true);
				} else {
					sponsorURLText.setEnabled(false);
				}
				getWizard().getContainer().updateButtons();
			}
		});
		joinButton.setSelection(true);
		joinButton.setBounds(50, 59, 331, 16);
		joinButton.setText("Join an existing OCP network.");

		
		Label lblListenerUrl = new Label(container, SWT.NONE);
		lblListenerUrl.setBounds(50, 107, 181, 13);
		lblListenerUrl.setText("Sponsor URL :");

		sponsorURLText = new Text(container, SWT.BORDER);
		sponsorURLText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				if (sponsorURLText.getText().equals("")) {
					bIsPageComplete = false;
					getWizard().getContainer().updateButtons();
				} else if (bIsPageComplete == false) {
					bIsPageComplete = true;
					getWizard().getContainer().updateButtons();
				}

			}
		});
		sponsorURLText.setText("tcp://localhost:22220");
		sponsorURLText.setBounds(50, 126, 181, 19);
	}
	@Override
	public boolean isPageComplete() {
		return bIsPageComplete ;
	}

}
