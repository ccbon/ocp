package org.ocpteam.component;

import org.ocpteam.core.IContainer;
import org.ocpteam.core.IComponent;



public abstract class Agent implements IComponent {
	
	@Override
	public void setParent(IContainer parent) {
		this.ds = (DataSource) parent;		
	}

	private boolean bIsConnected = false;

	public DataSource ds;
	
	public void connect() throws Exception {
		if (bIsConnected == true) {
			throw new Exception("Already connected");
		}
		onConnect();
		bIsConnected = true;
	}

	public void disconnect() throws Exception {
		if (bIsConnected == false) {
			throw new Exception("Already disconnected");
		}
		onDisconnect();
		bIsConnected = false;
	}

	protected abstract void onConnect() throws Exception;

	protected abstract void onDisconnect();

	public boolean isConnected() {
		return bIsConnected;
	}
}
