package org.ocpteam.protocol.dht;

import java.util.Properties;

import org.ocpteam.component.DSPDataSource;
import org.ocpteam.component.MapDataModel;
import org.ocpteam.interfaces.IDataModel;
import org.ocpteam.misc.JLG;

public class DHTDataSource extends DSPDataSource {

	public DHTDataSource() throws Exception {
		super();
		addComponent(IDataModel.class, new MapDataModel());
		
	}
	
	@Override
	public String getProtocol() {
		return "DHT";
	}

	@Override
	protected Properties getNetworkProperties() {
		return JLG.extractProperties(getConfig(), "network");
	}

}
