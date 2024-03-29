package org.ocpteamx.protocol.ftp.swt;

import java.net.URI;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.ocpteam.ui.swt.DataSourceWindow;
import org.ocpteam.ui.swt.IScenario;
import org.ocpteamx.protocol.ftp.FTPDataSource;

public class FTPNewDataSourceWizard extends Wizard implements IScenario {
	FirstWizardPage p1;
	private DataSourceWindow w;
	private boolean bSucceeded;

	public FTPNewDataSourceWizard() {
		setWindowTitle("FTP Wizard");
	}

	@Override
	public void addPages() {
		p1 = new FirstWizardPage();
		addPage(p1);
	}

	@Override
	public boolean performFinish() {
		try {
			w.ds = new FTPDataSource();
			w.ds.init();
			URI uri = new URI("ftp://" + p1.serverHostnameText.getText() + ":"
					+ p1.portText.getText() + p1.defaultLocalDirText.getText());
			w.ds.setURI(uri);
		} catch (Exception e) {
			e.printStackTrace();
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
		IWizardPage[] pages = getPages();
		for (int i = 0; i < pages.length; i++) {
			if (!pages[i].isPageComplete()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void run() throws Exception {
		Display display = Display.getDefault();
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
