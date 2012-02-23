package org.ocpteam.protocol.ocp;

import java.io.File;

import org.ocpteam.layer.rsp.AgentConfig;



public class OCPConfig extends AgentConfig {

	private static final String AGENT_PROPERTIES_FILE = "agent.properties";
	
	@Override
	public boolean requiresConfigFile() {
		return true;
	}
	
	@Override
	public File getConfigFile() {
		return new File(AGENT_PROPERTIES_FILE);
	}


}
