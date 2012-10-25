package org.ocpteamx.protocol.dht5;

import org.ocpteam.component.FTPPersistentFileMap;
import org.ocpteam.interfaces.IPersistentMap;

/**
 * DHT5 is a distributed hashtable based on AddressDataSource. The data model is
 * file system based.
 * 
 * 
 * 
 */
public class DHT5v3DataSource extends DHT5v2DataSource {

	public DHT5v3DataSource() throws Exception {
		super();
		replaceComponent(IPersistentMap.class, new FTPPersistentFileMap());
	}

	@Override
	public String getProtocolName() {
		return "DHT5v3";
	}

}
