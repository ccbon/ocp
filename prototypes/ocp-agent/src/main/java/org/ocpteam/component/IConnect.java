package org.ocpteam.component;

public interface IConnect {
	public void connect() throws Exception;
	public void disconnect() throws Exception;
	public boolean testConnection();
}
