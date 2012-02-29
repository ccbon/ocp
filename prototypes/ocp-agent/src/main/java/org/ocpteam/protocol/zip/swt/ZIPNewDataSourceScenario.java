package org.ocpteam.protocol.zip.swt;

import java.io.File;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.ocpteam.layer.rsp.DataSource;
import org.ocpteam.misc.JLG;
import org.ocpteam.protocol.zip.ZipUtils;
import org.ocpteam.ui.jlg.swt.NewDataSourceScenario;

public class ZIPNewDataSourceScenario implements NewDataSourceScenario {

	@Override
	public DataSource run() throws Exception {
		Display display = Display.getDefault();
		final Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		FileDialog fd = new FileDialog(shell, SWT.SAVE);
		fd.setText("Save");
		fd.setFilterPath(System.getProperty("user.home"));
		String[] filterExt = { "*.zip", "*.tar.gz", "*.*" };
		fd.setFilterExtensions(filterExt);
		String selected = fd.open();
		JLG.debug(selected);
		shell.dispose();
		if (selected == null) {
			return null;
		}
		File file = new File(selected);
		if (!file.exists()) {
			file = ZipUtils.createEmptyFile(selected);
		}
		return new DataSource(file);
	}

}
