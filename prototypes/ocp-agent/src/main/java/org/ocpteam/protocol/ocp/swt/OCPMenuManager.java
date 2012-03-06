package org.ocpteam.protocol.ocp.swt;

import org.ocpteam.ui.jlg.swt.DataSourceWindow;
import org.ocpteam.ui.jlg.swt.DynamicMenuManager;

public class OCPMenuManager extends DynamicMenuManager {
	
	public OCPMenuManager(String text, String id) {
		super(text, id);
	}

	@Override
	public void init(DataSourceWindow w) {
		add(new ViewContactTabAction(w));
	}

}
