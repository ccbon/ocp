package org.ocpteam.protocol.dht5;

import org.ocpteam.component.AddressDataSource;
import org.ocpteam.component.PersistentFileMap;
import org.ocpteam.component.AddressUserCreation;
import org.ocpteam.component.UserManagement;
import org.ocpteam.fs.BFSDataModel;
import org.ocpteam.interfaces.IAuthenticable;
import org.ocpteam.interfaces.IDataModel;
import org.ocpteam.interfaces.IPersistentMap;
import org.ocpteam.interfaces.IUserCreation;
import org.ocpteam.interfaces.IUserManagement;

/**
 * DHT5 is a distributed hashtable based on AddressDataSource. The data model is
 * file system based.
 * 
 * 
 * 
 */
public class DHT5v1DataSource extends AddressDataSource {

	public DHT5v1DataSource() throws Exception {
		super();
		addComponent(IDataModel.class, new BFSDataModel());
		addComponent(IPersistentMap.class, new PersistentFileMap());
		addComponent(IUserManagement.class, new UserManagement());
		AddressUserCreation uc = new AddressUserCreation();
		addComponent(IUserCreation.class, uc);
		addComponent(IAuthenticable.class, uc);
	}

	@Override
	public String getProtocolName() {
		return "DHT5v1";
	}

}
