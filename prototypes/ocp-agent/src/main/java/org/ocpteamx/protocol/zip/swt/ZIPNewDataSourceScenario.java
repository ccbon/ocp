package org.ocpteamx.protocol.zip.swt;

import org.ocpteam.ui.swt.DataSourceWindow;
import org.ocpteam.ui.swt.IScenario;

public class ZIPNewDataSourceScenario implements IScenario {

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
