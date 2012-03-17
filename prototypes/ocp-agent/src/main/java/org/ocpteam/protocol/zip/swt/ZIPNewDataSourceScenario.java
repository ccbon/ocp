package org.ocpteam.protocol.zip.swt;

import org.ocpteam.ui.swt.DataSourceWindow;
import org.ocpteam.ui.swt.Scenario;

public class ZIPNewDataSourceScenario implements Scenario {

	private DataSourceWindow w;

	@Override
	public void run() throws Exception {
		w.ds.newTemp();
	}

	@Override
	public void setWindow(DataSourceWindow w) {
		this.w = w;

	}

}
