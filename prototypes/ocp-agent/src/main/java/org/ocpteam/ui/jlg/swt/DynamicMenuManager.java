package org.ocpteam.ui.jlg.swt;

import org.eclipse.jface.action.MenuManager;

public abstract class DynamicMenuManager extends MenuManager {
	
	public DynamicMenuManager(String text, String id) {
		super(text, id);
	}

	public abstract void init(DataSourceWindow dataSourceWindow);

}
