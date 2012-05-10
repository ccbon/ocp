package org.ocpteamx.protocol.zip.swt;

import java.util.ListResourceBundle;

public class ZIPSWTResource extends ListResourceBundle {

	@Override
	protected Object[][] getContents() {
		return new Object[][] {
				{ "NewDataSourceScenario", new ZIPNewDataSourceScenario() },
				{ "menu", new ZIPMenuManager("ZIP", "protocolMenu") },
				{ "file_ext", new String[] { "*.zip", "*.*" } } };
	}

}
