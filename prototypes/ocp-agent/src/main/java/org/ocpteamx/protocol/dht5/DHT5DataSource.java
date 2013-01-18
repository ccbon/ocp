package org.ocpteamx.protocol.dht5;

import org.ocpteam.component.AddressDataSource;
import org.ocpteam.component.PersistentFileMap;
import org.ocpteam.component.UserManagement;
import org.ocpteam.fs.BFSDataModel;
import org.ocpteam.interfaces.IDataModel;
import org.ocpteam.interfaces.IDataStore;
import org.ocpteam.interfaces.IUserManagement;

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
		replaceComponent(IDataStore.class, new PersistentFileMap());
		addComponent(IUserManagement.class, new UserManagement());
	}

	@Override
	public String getProtocolName() {
		return "DHT5";
	}

}
