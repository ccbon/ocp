package org.ocpteamx.protocol.dht5;

import org.ocpteam.component.AddressDataSource;
import org.ocpteam.component.AddressUserCreation;
import org.ocpteam.component.NATTraversal;
import org.ocpteam.component.PersistentFileMap;
import org.ocpteam.component.Security;
import org.ocpteam.component.TCPListener;
import org.ocpteam.component.UDPListener;
import org.ocpteam.component.UserBackup;
import org.ocpteam.component.UserManagement;
import org.ocpteam.fs.BFSDataModel;
import org.ocpteam.interfaces.IAuthenticable;
import org.ocpteam.interfaces.IDataModel;
import org.ocpteam.interfaces.IPersistentMap;
import org.ocpteam.interfaces.ISecurity;
import org.ocpteam.interfaces.IUser;
import org.ocpteam.interfaces.IUserBackup;
import org.ocpteam.interfaces.IUserCreation;
import org.ocpteam.interfaces.IUserManagement;
import org.ocpteam.serializable.SecureUser;

/**
 * DHT5 is a distributed hashtable based on AddressDataSource. The data model is
 * file system based.
 * 
 * 
 * 
 */
public class DHT5v2DataSource extends AddressDataSource {

	public DHT5v2DataSource() throws Exception {
		super();
		addComponent(IDataModel.class, new BFSDataModel());
		addComponent(IPersistentMap.class, new PersistentFileMap());
		addComponent(IUserManagement.class, new UserManagement());
		addComponent(IUser.class, new SecureUser());
		AddressUserCreation uc = new AddressUserCreation();
		addComponent(IUserCreation.class, uc);
		addComponent(IAuthenticable.class, uc);
		addComponent(ISecurity.class, new Security());
		addComponent(IUserBackup.class, new UserBackup());
		
		getComponent(TCPListener.class).addComponent(NATTraversal.class);
		getComponent(UDPListener.class).addComponent(NATTraversal.class);
	}

	@Override
	public String getProtocolName() {
		return "DHT5v2";
	}

}
