package org.ocpteam.protocol.ftp.swt;

import java.net.URI;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.ocpteam.layer.rsp.DataSource;
import org.ocpteam.misc.JLG;
import org.ocpteam.ui.jlg.swt.NewDataSourceScenario;

public class FTPNewDataSourceWizard extends Wizard implements NewDataSourceScenario {
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
			ds = new DataSource(uri);
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
	public DataSource run() {
		Display display = Display.getDefault();
		final Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		WizardDialog dialog = new WizardDialog(shell, this);
		dialog.open();
		shell.dispose();
		return ds;
	}
}
