package com.guenego.ocp.gui.console;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.guenego.misc.JLG;

public class NewUserWizard extends Wizard {

	
	public static void start(Display display) {
		final Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());

		WizardDialog dialog = new WizardDialog(shell, new NewUserWizard());
		dialog.open();
		JLG.debug("about to dispose shell");
		//shell.dispose();
	}

	public NewUserWizard() {
		setWindowTitle("Create new user Wizard");
	}

	@Override
	public void addPages() {
		addPage(new NewUserFormWizardPage());
		addPage(new NewUserCaptchaWizardPage());
	}

	@Override
	public boolean performFinish() {
		return false;
	}

}
