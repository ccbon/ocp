package org.ocpteam.component;

import org.ocpteam.module.DSPModule;


public class DSPProtocol extends Protocol {

	public DSPProtocol() throws Exception {
		addComponent(DSPModule.class);
	}


}
