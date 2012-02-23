package org.ocpteam.layer.rsp;

import java.util.HashMap;
import java.util.Map;

public abstract class Agent {

	public AgentConfig cfg;

	protected boolean bIsConnected = false;

	protected Map<String, Object> assistantMap;

	public Agent() {
		assistantMap = new HashMap<String, Object>();
	}

	public void setConfig(AgentConfig cfg) throws Exception {
		this.cfg = cfg;
	}
	
	public void readConfig() throws Exception {
	}

	public abstract void connect() throws Exception;

	public abstract void disconnect();

	public abstract boolean allowsUserCreation();

	public abstract User login(String login, Object challenge) throws Exception;

	public abstract void logout(User user) throws Exception;

	public abstract String getProtocolName();

	public abstract String getName();

	public String getHelpURL() {
		return "http://code.google.com/p/ocp/wiki/Help";
	}

	public abstract FileSystem getFileSystem(User user);

	public abstract boolean autoConnect();

	public boolean isConnected() {
		return bIsConnected;
	}

	public boolean authenticatesWithSSH() {
		return false;
	}

	public abstract boolean isOnlyClient();

	public Object getAssistant(String key) {
		return assistantMap.get(key);
	}

	public Object setAssistant(String key, Object assistant) {
		return assistantMap.put(key, assistant);
	}

	public abstract boolean usesAuthentication();
}
