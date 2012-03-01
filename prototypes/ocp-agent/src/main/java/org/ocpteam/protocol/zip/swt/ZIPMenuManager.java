package org.ocpteam.protocol.zip.swt;

import org.ocpteam.ui.jlg.swt.DataSourceWindow;
import org.ocpteam.ui.jlg.swt.DynamicMenuManager;
import org.ocpteam.ui.jlg.swt.HelpAction;

public class ZIPMenuManager extends DynamicMenuManager {
	
	public ZIPMenuManager(String text, String id) {
		super(text, id);
	}

	@Override
	public void init(DataSourceWindow w) {
		add(new HelpAction(w));
		
	}

}
