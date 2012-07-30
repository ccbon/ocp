package org.ocpteamx.protocol.dht4;

import org.ocpteam.component.AddressDataSource;
import org.ocpteam.component.AddressMapDataModel;
import org.ocpteam.interfaces.IDataModel;

/**
 * DHT3 is a distributed hashtable based on DHT1. The storage is now the
 * IAddressMap component.
 * 
 * Strategies:
 * 
 * 
 */
public class DHT4DataSource extends AddressDataSource {

	public AddressMapDataModel dm;

	public DHT4DataSource() throws Exception {
		super();
		addComponent(IDataModel.class, new AddressMapDataModel());
	}

	@Override
	public void init() throws Exception {
		super.init();
		dm = (AddressMapDataModel) getComponent(IDataModel.class);
	}

	@Override
	public String getProtocolName() {
		return "DHT4";
	}

}