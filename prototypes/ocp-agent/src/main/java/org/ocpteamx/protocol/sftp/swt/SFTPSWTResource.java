package org.ocpteamx.protocol.sftp.swt;

import java.util.ListResourceBundle;

public class SFTPSWTResource extends ListResourceBundle {

	@Override
	protected Object[][] getContents() {
		return new Object[][] {
				{ "NewDataSourceScenario", new SFTPNewDataSourceWizard() },
				{ "SignInScenario", new SFTPNewDataSourceWizard() },
				{ "file_ext", new String[] { "*.uri", "*.*" } } };
	}

}
