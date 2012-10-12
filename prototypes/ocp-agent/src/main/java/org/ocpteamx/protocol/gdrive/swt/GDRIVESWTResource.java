package org.ocpteamx.protocol.gdrive.swt;

import java.util.ListResourceBundle;

public class GDRIVESWTResource extends ListResourceBundle {

	@Override
	protected Object[][] getContents() {
		return new Object[][] {
				{ "SignInScenario", new GDRIVESignInWizard() },
				{ "file_ext", new String[] { "*.uri", "*.*" } } };
	}

}
