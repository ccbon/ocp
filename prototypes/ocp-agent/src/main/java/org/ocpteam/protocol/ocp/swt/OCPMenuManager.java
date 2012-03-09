package org.ocpteam.protocol.ocp.swt;

import org.ocpteam.ui.swt.DynamicMenuManager;

public class OCPMenuManager extends DynamicMenuManager {
	
	public OCPMenuManager(String text, String id) {
		super(text, id);
	}

	@Override
	public void init() {
		add(new ViewContactTabAction(w));
		add(new RemoveStorageAction(w));
	}

}
