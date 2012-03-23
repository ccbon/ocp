package org.ocpteam.protocol.dht;

import org.ocpteam.component.DSPDataSource;
import org.ocpteam.component.MapDataModel;
import org.ocpteam.interfaces.IDataModel;

public class DHTDataSource extends DSPDataSource {

	public DHTDataSource() throws Exception {
		super();
		addComponent(IDataModel.class, new MapDataModel());
		
	}
	
	@Override
	public String getProtocol() {
		return "DHT";
	}

	

}
