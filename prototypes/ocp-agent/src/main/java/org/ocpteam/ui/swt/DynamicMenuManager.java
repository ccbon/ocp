package org.ocpteam.ui.swt;

import org.eclipse.jface.action.MenuManager;

public abstract class DynamicMenuManager extends MenuManager {
	
	protected DataSourceWindow w;

	public DynamicMenuManager(String text, String id) {
		super(text, id);
	}

	public abstract void init();

	public void setParent(DataSourceWindow parent) {
		this.w = parent;
	}

}
