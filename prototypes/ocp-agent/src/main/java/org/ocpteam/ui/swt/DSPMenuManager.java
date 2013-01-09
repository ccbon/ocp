package org.ocpteam.ui.swt;

import org.eclipse.jface.action.Separator;


public class DSPMenuManager extends DynamicMenuManager {

	public DSPMenuManager(String text, String id) {
		super(text, id);
	}

	@Override
	public void init() {
		add(new ViewNetworkPropertiesAction(w));
		add(new ViewContactTabAction(w));
		add(new Separator());
		add(new EditDataSouceAction(w));
	}

}
