package org.ocpteam.protocol.dht;

import org.ocpteam.component.Agent;
import org.ocpteam.component.DataSource;
import org.ocpteam.component.IDataModel;
import org.ocpteam.component.MapDataModel;
import org.ocpteam.component.Server;

public class DHTDataSource extends DataSource {

	public DHTDataSource() throws Exception {
		getDesigner().add(Agent.class, new Agent());
		//getDesigner().add(Client.class, new DHTClient());
		getDesigner().add(Server.class, new Server());
		getDesigner().add(IDataModel.class, new MapDataModel());
	}
	
	@Override
	public String getProtocol() {
		return "DHT";
	}

}
