package org.ocpteam.protocol.dht;

import org.ocpteam.layer.rsp.PropertiesDataSource;

public class DHTDataSource extends PropertiesDataSource {

	public DHTDataSource() throws Exception {
		//getDesigner().add(Agent.class, new DHTAgent());
	}
	
	@Override
	public String getProtocol() {
		return "DHT";
	}

}
