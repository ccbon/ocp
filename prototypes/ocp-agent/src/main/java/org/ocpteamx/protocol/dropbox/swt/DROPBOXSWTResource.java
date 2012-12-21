package org.ocpteamx.protocol.dropbox.swt;

import java.util.ListResourceBundle;

public class DROPBOXSWTResource extends ListResourceBundle {

	@Override
	protected Object[][] getContents() {
		return new Object[][] {
				{ "SignInScenario", new DROPBOXSignInWizard() }
		};
	}

}
