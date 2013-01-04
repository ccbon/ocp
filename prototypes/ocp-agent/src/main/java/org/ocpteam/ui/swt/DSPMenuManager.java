package org.ocpteam.ui.swt;


public class DSPMenuManager extends DynamicMenuManager {

	public DSPMenuManager(String text, String id) {
		super(text, id);
	}

	@Override
	public void init() {
		add(new ViewNetworkPropertiesAction(w));
		add(new ViewContactTabAction(w));
	}

}
