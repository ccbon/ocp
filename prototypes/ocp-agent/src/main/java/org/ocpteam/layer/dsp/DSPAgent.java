package org.ocpteam.layer.dsp;

import org.ocpteam.component.Agent;

/**
 * Provide an agent class evolving in a distributed environment.
 * All the contact features are defined here.
 *
 */
public abstract class DSPAgent extends Agent {
	
	public DSPAgent() {
		super();
	}
	


	public abstract void removeStorage();

}
