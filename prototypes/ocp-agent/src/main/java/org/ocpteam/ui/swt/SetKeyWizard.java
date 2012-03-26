package org.ocpteam.ui.swt;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.ocpteam.misc.swt.QuickMessage;

public class SetKeyWizard extends Wizard {

	private DataSourceWindow w;
	private SetKeyWizardPage p1;

	public SetKeyWizard(DataSourceWindow w) {
		setWindowTitle("New Wizard");
		this.w = w;
	}

	@Override
	public void addPages() {
		p1 = new SetKeyWizardPage();
		addPage(p1);
	}

	@Override
	public boolean performFinish() {
		MapComposite mapComposite = (MapComposite) w.explorerComposite;
		try {
		mapComposite.set(p1.keyText.getText(), p1.valueText.getText());
		} catch (Exception e) {
			e.printStackTrace();
			QuickMessage.error(w.getShell(), "Cannot set");
		}
		return true;
	}

	public void start() {
		Display display = w.getShell().getDisplay();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		WizardDialog dialog = new WizardDialog(shell, this);
		dialog.open();
		shell.dispose();
	}

}
