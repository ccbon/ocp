package org.ocpteamx.protocol.dht4;

import org.ocpteam.component.AddressDataSource;
import org.ocpteam.component.AddressMapDataModel;
import org.ocpteam.interfaces.IDataModel;

/**
 * DHT4 address topology is a single ring address topology.
 * DHT4 data model is a map.
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
