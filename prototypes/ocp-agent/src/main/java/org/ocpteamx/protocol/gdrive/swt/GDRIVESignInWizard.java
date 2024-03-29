package org.ocpteamx.protocol.gdrive.swt;

import java.net.URI;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.ocpteam.interfaces.IAuthenticable;
import org.ocpteam.interfaces.IUserManagement;
import org.ocpteam.misc.LOG;
import org.ocpteam.misc.swt.QuickMessage;
import org.ocpteam.ui.swt.DataSourceWindow;
import org.ocpteam.ui.swt.IScenario;
import org.ocpteamx.protocol.gdrive.GDriveClient;

public class GDRIVESignInWizard extends Wizard implements IScenario {

	private GDRIVESignInWizardPage p1;
	private DataSourceWindow w;
	private boolean bSucceeded;

	public GDRIVESignInWizard() {
		setWindowTitle("GDrive Sign In Wizard");
	}

	@Override
	public void addPages() {
		p1 = new GDRIVESignInWizardPage();
		addPage(p1);
	}

	@Override
	public boolean performFinish() {
		LOG.info("sign in user");
		try {
			String code = p1.codeText.getText();
			URI uri = new URI("gdrive://" + code);
			w.ds.setURI(uri);
			IUserManagement um = w.ds.getComponent(IUserManagement.class);
			um.setUsername("not used");
			w.ds.getComponent(IAuthenticable.class).setChallenge(code);
		} catch (Exception e) {
			e.printStackTrace();
			QuickMessage.error(getShell(), "Cannot connect");
		}

		return true;
	}

	@Override
	public boolean performCancel() {
		w.ds = null;
		return super.performCancel();
	}

	@Override
	public boolean canFinish() {
		return p1.isPageComplete();
	}

	@Override
	public void run() throws Exception {
		GDriveClient c = (GDriveClient) w.ds.getComponent(IAuthenticable.class);
		Program.launch(c.getURL());
		Display display = w.getShell().getDisplay();
		final Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		WizardDialog dialog = new WizardDialog(shell, this);
		bSucceeded = (dialog.open() == 0);
		shell.dispose();
	}

	@Override
	public void setWindow(DataSourceWindow w) {
		this.w = w;
	}

	@Override
	public boolean succeeded() {
		return bSucceeded;
	}

}
