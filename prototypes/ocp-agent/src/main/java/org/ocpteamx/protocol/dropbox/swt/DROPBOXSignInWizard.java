package org.ocpteamx.protocol.dropbox.swt;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.ocpteam.interfaces.IAuthenticable;
import org.ocpteam.ui.swt.DataSourceWindow;
import org.ocpteam.ui.swt.IScenario;
import org.ocpteamx.protocol.dropbox.DropboxClient;

public class DROPBOXSignInWizard extends Wizard implements IScenario {
	
	private DataSourceWindow w;
	private DROPBOXSignInWizardPage p1;

	public DROPBOXSignInWizard() {
		setWindowTitle("Dropbox Sign In Wizard");
	}

	@Override
	public void setWindow(DataSourceWindow w) {
		this.w = w;
	}
	
	@Override
	public void addPages() {
		p1 = new DROPBOXSignInWizardPage();
		addPage(p1);
	}

	@Override
	public void run() throws Exception {		
		DropboxClient c = (DropboxClient) w.ds.getComponent(IAuthenticable.class);
		Program.launch(c.getURL());
		Display display = w.getShell().getDisplay();
		final Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		WizardDialog dialog = new WizardDialog(shell, this);
		dialog.open();
		shell.dispose();
	}

	@Override
	public boolean performFinish() {
		return true;
	}
	
	@Override
	public boolean performCancel() {
		w.ds = null;
		return super.performCancel();
	}

}
