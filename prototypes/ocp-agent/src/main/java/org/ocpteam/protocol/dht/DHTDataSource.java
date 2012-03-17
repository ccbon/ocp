package org.ocpteam.protocol.dht;

import java.util.Properties;

import org.ocpteam.component.IDataModel;
import org.ocpteam.component.MapDataModel;
import org.ocpteam.layer.dsp.DSPDataSource;
import org.ocpteam.misc.JLG;

public class DHTDataSource extends DSPDataSource {

	public DHTDataSource() throws Exception {
		super();
		getDesigner().add(IDataModel.class, new MapDataModel());
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
