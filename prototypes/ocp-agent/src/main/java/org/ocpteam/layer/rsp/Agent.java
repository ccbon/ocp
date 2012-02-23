package org.ocpteam.layer.rsp;

import java.util.HashMap;
import java.util.Map;

public abstract class Agent {

	public AgentConfig cfg;

	protected boolean bIsStarted = false;

	protected Map<String, Object> assistantMap;

	public Agent() {
		assistantMap = new HashMap<String, Object>();
	}

	public void setConfig(AgentConfig cfg) throws Exception {
		this.cfg = cfg;
	}
	
	public void readConfig() throws Exception {
	}

	public abstract void start() throws Exception;

	public abstract void stop();

	public abstract boolean allowsUserCreation();

	public abstract User login(String login, Object challenge) throws Exception;

	public abstract void logout(User user) throws Exception;

	public abstract String getProtocolName();

	public abstract String getName();

	public String getHelpURL() {
		return "http://code.google.com/p/ocp/wiki/Help";
	}

	public abstract FileSystem getFileSystem(User user);

	public abstract boolean autoStarts();

	public boolean isStarted() {
		return bIsStarted;
	}

	public boolean connectsWithSSH() {
		return false;
	}

	public abstract boolean isOnlyClient();

	public Object getAssistant(String key) {
		return assistantMap.get(key);
	}

	public Object setAssistant(String key, Object assistant) {
		return assistantMap.put(key, assistant);
	}
}
