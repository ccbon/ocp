package org.ocpteam.layer.dsp;

import java.util.Properties;

import org.ocpteam.layer.rsp.Agent;
import org.ocpteam.layer.rsp.PropertiesDataSource;
import org.ocpteam.misc.JLG;

/**
 * Provide an agent class evolving in a distributed environment.
 * All the contact features are defined here.
 *
 */
public abstract class DSPAgent extends Agent {

	public Properties cfg;
	
	public DSPAgent(PropertiesDataSource ds) {
		super(ds);
		cfg = ds.getProperties();
	}
	
	public abstract String getName();
	
	public boolean isFirstAgent() {
		if (cfg == null) {
			JLG.debug("p is null");
		}
		String s = cfg.getProperty("server", "no");
		return s.equalsIgnoreCase("yes")
				&& cfg.getProperty("server.isFirstAgent", "no").equalsIgnoreCase(
						"yes");
	}

	public abstract boolean hasStorage();

	public abstract void removeStorage();

}
