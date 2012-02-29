package org.ocpteam.protocol.zip.swt;

import java.util.ListResourceBundle;

public class ZIPSWTResource extends ListResourceBundle {

	@Override
	protected Object[][] getContents() {
		return new Object[][] { { "NewDataSourceScenario", new ZIPNewDataSourceScenario() }, { "CancelKey", "Cancel" }, };
	}

}
