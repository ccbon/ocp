package org.ocpteam.protocol.dht5;

import org.ocpteam.component.AddressDataSource;
import org.ocpteam.component.PersistentFileMap;
import org.ocpteam.component.UserIdentification;
import org.ocpteam.fs.BFSDataModel;
import org.ocpteam.interfaces.IDataModel;
import org.ocpteam.interfaces.IPersistentMap;

/**
 * DHT5 is a distributed hashtable based on AddressDataSource. The data model is
 * file system based.
 * 
 * 
 * 
 */
public class DHT5DataSource extends AddressDataSource {

	public DHT5DataSource() throws Exception {
		super();
		addComponent(IDataModel.class, new BFSDataModel());
		addComponent(IPersistentMap.class, new PersistentFileMap());
		addComponent(UserIdentification.class);
	}

	@Override
	public String getProtocolName() {
		return "DHT5";
	}

}
