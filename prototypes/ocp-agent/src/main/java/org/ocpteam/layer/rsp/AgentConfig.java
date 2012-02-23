package org.ocpteam.layer.rsp;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.Set;

import org.ocpteam.misc.JLG;

public class AgentConfig {

	public Agent agent;
	private Properties p;
	
	public static AgentConfig newInstance(Agent agent) {
		String packageName = agent.getClass().getPackage().getName();
		AgentConfig result;
		try {
			result = (AgentConfig) Class.forName(packageName + "." + agent.getProtocolName() + "Config").newInstance();
		} catch (Exception e) {
			result = new AgentConfig();
		}
		result.agent = agent;
		result.p = new Properties();
		return result;
	}

	public boolean requiresConfigFile() {
		return false;
	}

	public boolean isConfigFilePresent() {
		return getConfigFile().isFile();
	}

	public File getConfigFile() {
		return new File(agent.getProtocolName().toLowerCase() + ".properties");
	}

	public void loadConfigFile() throws Exception {
		if (isConfigFilePresent()) {
			p.load(new FileInputStream(getConfigFile()));
		}
	}

	public void loadProperties(Properties p) {
		this.p = p;
	}
	
	public void storeConfigFile() {
		JLG.storeConfig(p, getConfigFile().getAbsolutePath());
	}

	public String getProperty(String key, String defaultValue) {
		return p.getProperty(key, defaultValue);
	}

	public String getProperty(String key) {
		return p.getProperty(key);
	}

	public Set<String> stringPropertyNames() {
		return p.stringPropertyNames();
	}

	public void setProperty(String key, String value) {
		p.setProperty(key, value);
	}


}
