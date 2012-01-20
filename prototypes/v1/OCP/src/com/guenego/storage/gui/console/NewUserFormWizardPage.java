package com.guenego.storage.gui.console;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.guenego.misc.JLG;
import com.guenego.ocp.Captcha;
import com.guenego.ocp.OCPAgent;

public class NewUserFormWizardPage extends WizardPage {
	private Text usernameText;
	private Text passwordText;

	/**
	 * Create the wizard.
	 */
	public NewUserFormWizardPage() {
		super("NewUserFormWizardPage");
		setTitle("Create new user");
		setDescription("Please fill this form.");
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		
		Label lblUsername = new Label(container, SWT.NONE);
		lblUsername.setBounds(57, 27, 49, 13);
		lblUsername.setText("Username");
		
		usernameText = new Text(container, SWT.BORDER);
		usernameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				getWizard().getContainer().updateButtons();
			}
		});
		usernameText.setBounds(57, 46, 153, 19);
		
		Label lblPassword = new Label(container, SWT.NONE);
		lblPassword.setBounds(57, 87, 49, 13);
		lblPassword.setText("Password");
		
		passwordText = new Text(container, SWT.BORDER | SWT.PASSWORD);
		passwordText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				getWizard().getContainer().updateButtons();
			}
		});
		passwordText.setBounds(57, 106, 153, 19);
		container.setTabList(new Control[]{usernameText, passwordText});
		usernameText.setFocus();
	}
	
	@Override
	public boolean canFlipToNextPage() {
		JLG.debug("canFlipToNextPage");
		if (usernameText.getText().equals("")) {
			return false;
		}
		if (passwordText.getText().equals("")) {
			return false;
		}

		return super.canFlipToNextPage();
	}
	
	@Override
	public IWizardPage getNextPage() {
		// TODO Auto-generated method stub
		JLG.debug("getNextPage");
		return super.getNextPage();
	}

	public void onNextPage() throws Exception {
		NewUserWizard wizard = (NewUserWizard) getWizard();
		OCPAgent agent = (OCPAgent) wizard.getAgent();
		wizard.setUsername(usernameText.getText());
		wizard.setPassword(passwordText.getText());
		Captcha c = agent.wantToCreateUser(usernameText.getText(), passwordText.getText());
		wizard.setCaptcha(c);
	}
	
}
