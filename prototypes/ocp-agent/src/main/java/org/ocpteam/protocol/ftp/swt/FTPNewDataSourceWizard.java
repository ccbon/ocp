package org.ocpteam.protocol.ftp.swt;

import java.net.URI;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.ocpteam.layer.rsp.DataSource;
import org.ocpteam.protocol.ftp.FTPDataSource;
import org.ocpteam.ui.jlg.swt.DataSourceWindow;
import org.ocpteam.ui.jlg.swt.Scenario;

public class FTPNewDataSourceWizard extends Wizard implements Scenario {
	FirstWizardPage p1;
	private DataSource ds;
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
			URI uri = new URI("ftp://" + p1.serverHostnameText.getText() + ":" + p1.portText.getText() + p1.defaultLocalDirText.getText());
			ds = new FTPDataSource(uri);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

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
	public void run(DataSourceWindow w) throws Exception {
		Display display = Display.getDefault();
		final Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		WizardDialog dialog = new WizardDialog(shell, this);
		dialog.open();
		shell.dispose();
		w.ds = ds;
	}
}
