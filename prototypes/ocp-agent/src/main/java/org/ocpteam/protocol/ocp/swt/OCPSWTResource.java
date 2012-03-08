package org.ocpteam.protocol.ocp.swt;

import java.util.ListResourceBundle;

public class OCPSWTResource extends ListResourceBundle {

	@Override
	protected Object[][] getContents() {
		return new Object[][] {
				{ "NewDataSourceScenario", new OCPNewDataSourceWizard() },
				{ "menu", new OCPMenuManager("OCP", "protocolMenu") }, 
				{ "file_ext", new String[] { "*.ocp", "*.*" }}};
	}

}
