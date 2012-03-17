package org.ocpteam.layer.dsp;

import java.util.Properties;

import org.ocpteam.component.Agent;
import org.ocpteam.component.Client;
import org.ocpteam.component.DataSource;
import org.ocpteam.component.IDataModel;
import org.ocpteam.component.MapDataModel;
import org.ocpteam.component.Server;
import org.ocpteam.misc.JLG;

public abstract class DSPDataSource extends DataSource {

	protected Agent agent;
	protected Client client;

	protected Properties network;
	
	public DSPDataSource() throws Exception {
		agent = getDesigner().add(Agent.class);
		client = getDesigner().add(Client.class);
		getDesigner().add(Server.class, new Server());
		getDesigner().add(IDataModel.class, new MapDataModel());
	}
	
	@Override
	public void connect() throws Exception {
		// TODO: distributed datasource ?
		if (agent.isFirstAgent()) {
			JLG.debug("This is the first agent on the network");
			network = this.getNetworkProperties();
		} else {
			network = client.getNetworkProperties();
		}
		JLG.debug("network properties: " + JLG.propertiesToString(network));
	}

	protected abstract Properties getNetworkProperties();

}
