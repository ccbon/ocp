package org.ocpteam.ui.swt;

import org.eclipse.jface.action.Separator;
import org.ocpteam.interfaces.IDataStore;
import org.ocpteam.ui.swt.action.EditDataSouceAction;
import org.ocpteam.ui.swt.action.ViewContactTabAction;
import org.ocpteam.ui.swt.action.ViewDataStoreAction;
import org.ocpteam.ui.swt.action.ViewNetworkPropertiesAction;

public class DSPMenuManager extends DynamicMenuManager {

	public DSPMenuManager(String text, String id) {
		super(text, id);
	}

	@Override
	public void init() {
		add(new ViewNetworkPropertiesAction(w));
		add(new ViewContactTabAction(w));
		if (w.ds.usesComponent(IDataStore.class)) {
			add(new ViewDataStoreAction(w));
		}
		add(new Separator());
		add(new EditDataSouceAction(w));
		
	}

}
