package org.ocpteamx.protocol.dht6;

import org.ocpteam.component.FTPPersistentFileMap;
import org.ocpteam.interfaces.IDataStore;
import org.ocpteamx.protocol.dht5.DHT5v2DataSource;

/**
 * DHT5 is a distributed hashtable based on AddressDataSource. The data model is
 * file system based.
 * 
 * 
 * 
 */
public class DHT6DataSource extends DHT5v2DataSource {

	public DHT6DataSource() throws Exception {
		super();
		replaceComponent(IDataStore.class, new FTPPersistentFileMap());
	}

	@Override
	public String getProtocolName() {
		return "DHT6";
	}

}
