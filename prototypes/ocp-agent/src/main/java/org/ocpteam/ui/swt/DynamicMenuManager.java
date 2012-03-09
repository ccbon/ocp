package org.ocpteam.ui.swt;

import org.eclipse.jface.action.MenuManager;
import org.ocpteam.design.Functionality;

public abstract class DynamicMenuManager extends MenuManager implements Functionality<DataSourceWindow> {
	
	protected DataSourceWindow w;

	public DynamicMenuManager(String text, String id) {
		super(text, id);
	}

	public abstract void init();

	@Override
	public void setParent(DataSourceWindow parent) {
		this.w = parent;
		
	}

}
