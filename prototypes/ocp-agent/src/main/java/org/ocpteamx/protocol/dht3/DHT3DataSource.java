package org.ocpteamx.protocol.dht3;

import org.ocpteam.component.AddressDataSource;
import org.ocpteam.component.AddressMap;
import org.ocpteam.component.AddressMapDataModel;
import org.ocpteam.component.NodeMap;
import org.ocpteam.interfaces.IAddressMap;
import org.ocpteam.interfaces.IDataModel;
import org.ocpteam.interfaces.INodeMap;

/**
 * DHT3 is a distributed hashtable based on DHT1. The storage is now the
 * IAddressMap component.
 * 
 * Strategies:
 * 
 * 
 */
public class DHT3DataSource extends AddressDataSource {

	public AddressMapDataModel dm;

	public DHT3DataSource() throws Exception {
		super();
		replaceComponent(INodeMap.class, new NodeMap());
		replaceComponent(IAddressMap.class, new AddressMap());
		addComponent(IDataModel.class, new AddressMapDataModel());
	}
	
	@Override
	public void init() throws Exception {
		super.init();
		dm = (AddressMapDataModel) getComponent(IDataModel.class);
	}

	@Override
	public String getProtocolName() {
		return "DHT3";
	}

}
