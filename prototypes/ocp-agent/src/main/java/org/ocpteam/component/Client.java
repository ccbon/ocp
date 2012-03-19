package org.ocpteam.component;

import java.util.Properties;

import org.ocpteam.core.IComponent;
import org.ocpteam.core.IContainer;
import org.ocpteam.interfaces.IClient;

public class Client implements IComponent, IClient {

	protected DataSource ds;

	@Override
	public void setParent(IContainer parent) {
		this.ds = (DataSource) parent;	
	}
	
	@Override
	public IContainer getParent() {
		return ds;
	}

	@Override
	public void connect() throws Exception {
	}

	@Override
	public void disconnect() throws Exception {
	}

	/**
	 * @return the network properties coming from a server (or a peer)
	 */
	public Properties getNetworkProperties() throws Exception {
		return new Properties();
	}



}
