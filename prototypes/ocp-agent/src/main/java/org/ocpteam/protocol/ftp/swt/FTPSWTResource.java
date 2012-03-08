package org.ocpteam.protocol.ftp.swt;

import java.util.ListResourceBundle;

public class FTPSWTResource extends ListResourceBundle {

	@Override
	protected Object[][] getContents() {
		return new Object[][] {
				{ "NewDataSourceScenario", new FTPNewDataSourceWizard() },
				{ "CancelKey", "Cancel" },
				{ "file_ext", new String[] { "*.uri", "*.*" }}};
	}

}
