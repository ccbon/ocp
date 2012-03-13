package org.ocpteam.layer.dsp;

import java.util.Properties;

import org.ocpteam.functionality.Agent;
import org.ocpteam.misc.JLG;

/**
 * Provide an agent class evolving in a distributed environment.
 * All the contact features are defined here.
 *
 */
public abstract class DSPAgent extends Agent {

	public Properties cfg;
	
	public DSPAgent() {
		super();
	}
	
	public abstract String getName();
	
	public boolean isFirstAgent() {
		if (cfg == null) {
			JLG.debug("p is null");
		}
		String s = cfg.getProperty("server", "yes");
		return s.equalsIgnoreCase("yes")
				&& cfg.getProperty("server.isFirstAgent", "yes").equalsIgnoreCase(
						"yes");
	}

	public abstract void removeStorage();

}
