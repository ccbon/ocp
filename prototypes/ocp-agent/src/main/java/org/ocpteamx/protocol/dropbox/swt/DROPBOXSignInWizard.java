package org.ocpteamx.protocol.dropbox.swt;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.ocpteam.interfaces.IAuthenticable;
import org.ocpteam.misc.JLG;
import org.ocpteam.ui.swt.DataSourceWindow;
import org.ocpteam.ui.swt.IScenario;
import org.ocpteamx.protocol.dropbox.DropboxClient;

public class DROPBOXSignInWizard extends Wizard implements IScenario {

	private DataSourceWindow w;
	private DROPBOXSignInWizardPage p1;
	private DROPBOXSignInWizardPage2 p2;
	private DropboxClient c;
	private WizardDialog dialog;
	private WizardPage currentPage;
	private boolean bSucceeded;

	public DROPBOXSignInWizard() {
		setWindowTitle("Dropbox Sign In Wizard");
		p1 = new DROPBOXSignInWizardPage();
		p2 = new DROPBOXSignInWizardPage2();

		currentPage = p1;
	}

	@Override
	public void setWindow(DataSourceWindow w) {
		this.w = w;
	}

	@Override
	public void addPages() {

		addPage(p1);

	}

	@Override
	public void run() throws Exception {
		c = (DropboxClient) w.ds.getComponent(IAuthenticable.class);
		Display display = w.getShell().getDisplay();
		final Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		dialog = new WizardDialog(shell, this) {

			@Override
			protected void nextPressed() {
				JLG.debug("Next pressed");
				super.nextPressed();
			}

		};
		bSucceeded = (dialog.open() == 0);
		shell.dispose();
	}

	protected void informClient() {
		c.username = p1.text.getText();
		JLG.debug("username=" + c.username);

		boolean bRememberMe = p1.btnRememberMe.getSelection();
		JLG.debug("bRememberMe=" + bRememberMe);
		c.setRememberMe(bRememberMe);
	}

	@Override
	public boolean performFinish() {
		if (currentPage == p1) {
			try {
				informClient();
				c.authenticate();
			} catch (Exception e) {
				try {
					Program.launch(c.getURL());
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				addPage(p2);
				currentPage = p2;
				dialog.showPage(p2);
				return false;
			}

			return true;
		} else {
			try {
				c.authenticate();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}
	}

	@Override
	public boolean succeeded() {
		return bSucceeded;
	}

}
