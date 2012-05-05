package org.ocpteam.protocol.dht5;

import org.ocpteam.component.AddressDataSource;
import org.ocpteam.component.Authentication;
import org.ocpteam.component.PersistentFileMap;
import org.ocpteam.component.UserCreation;
import org.ocpteam.component.UserIdentification;
import org.ocpteam.fs.BFSDataModel;
import org.ocpteam.interfaces.IDataModel;
import org.ocpteam.interfaces.IPersistentMap;
import org.ocpteam.interfaces.IUserCreation;

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
		addComponent(UserIdentification.class, new Authentication());
		addComponent(IUserCreation.class, new UserCreation());
	}

	@Override
	public String getProtocolName() {
		return "DHT5v1";
	}

}
