package org.ocpteam.protocol.zip.swt;

import org.ocpteam.ui.swt.DynamicMenuManager;
import org.ocpteam.ui.swt.HelpAction;

public class ZIPMenuManager extends DynamicMenuManager {
	
	public ZIPMenuManager(String text, String id) {
		super(text, id);
	}

	@Override
	public void init() {
		add(new HelpAction(w));
		
	}

}
