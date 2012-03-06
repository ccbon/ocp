package org.ocpteam.protocol.zip.swt;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.ocpteam.layer.rsp.DataSource;
import org.ocpteam.misc.JLG;
import org.ocpteam.protocol.zip.ZipUtils;
import org.ocpteam.ui.swt.DataSourceWindow;
import org.ocpteam.ui.swt.Scenario;

public class ZIPNewDataSourceScenario implements Scenario {

	@Override
	public void run(DataSourceWindow w) throws Exception {
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
			return;
		}
		File file = new File(selected);
		if (!file.exists()) {
			file = ZipUtils.createEmptyFile(selected);
		}
		w.ds = DataSource.getInstance(file);
	}

}
