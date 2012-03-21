package org.ocpteam.component;

import java.util.Properties;

import org.ocpteam.interfaces.IClient;

public class Client extends DataSourceComponent implements IClient {

	/**
	 * @return the network properties coming from a server (or a peer)
	 */
	public Properties getNetworkProperties() throws Exception {
		return new Properties();
	}

}
