package org.ocpteam.protocol.zip.swt;

import org.ocpteam.protocol.zip.ZipUtils;
import org.ocpteam.ui.swt.DataSourceWindow;
import org.ocpteam.ui.swt.Scenario;

public class ZIPNewDataSourceScenario implements Scenario {

	private DataSourceWindow w;

	@Override
	public void run() throws Exception {
		w.ds.setTempFile(true);
		ZipUtils.createEmptyFile(w.ds.getFile().getAbsolutePath());
	}

	@Override
	public void setWindow(DataSourceWindow w) {
		this.w = w;

	}

}
