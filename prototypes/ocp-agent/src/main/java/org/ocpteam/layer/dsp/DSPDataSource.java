package org.ocpteam.layer.dsp;

import java.util.Iterator;
import java.util.Properties;

import org.ocpteam.component.Agent;
import org.ocpteam.component.Client;
import org.ocpteam.component.DataSource;
import org.ocpteam.component.IProtocol;
import org.ocpteam.component.MapDataModel;
import org.ocpteam.component.Server;
import org.ocpteam.component.TCPListener;
import org.ocpteam.core.IComponent;
import org.ocpteam.interfaces.IDataModel;
import org.ocpteam.interfaces.IListener;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.URL;
import org.ocpteam.protocol.ocp.Protocol;

public abstract class DSPDataSource extends DataSource {

	protected Agent agent;
	protected Client client;
	protected Server server;
	
	protected Properties network;
	
	
	public DSPDataSource() throws Exception {
		agent = getDesigner().add(Agent.class);
		client = getDesigner().add(Client.class);
		server = getDesigner().add(Server.class, new Server());
		
		getDesigner().add(TCPListener.class);
		
		getDesigner().get(TCPListener.class).getDesigner().add(IProtocol.class, new Protocol());
		
		getDesigner().add(IDataModel.class, new MapDataModel());
	}
	
	@Override
	public void connect() throws Exception {
		if (agent.isFirstAgent()) {
			JLG.debug("This is the first agent on the network");
			network = this.getNetworkProperties();
		} else {
			network = client.getNetworkProperties();
		}
		JLG.debug("network properties: " + JLG.propertiesToString(network));
		
		if (get("server", "yes").equals("yes")) {
			JLG.debug("starting the server");
			configureServer(server);
			server.start();
		}
	}
	
	protected void configureServer(Server server) throws Exception {
		Iterator<IComponent> it = getDesigner().iterator();
		while (it.hasNext()) {
			IComponent c = it.next();
			if (c instanceof IListener) {
				IListener l = (IListener) c;
				l.setUrl(new URL(get("listener.tcp.url", "tcp://localhost:22222")));
				server.getListeners().add(l);
			}
		}
	}

	@Override
	public void disconnect() throws Exception {
		if (server.isStarted()) {
			JLG.debug("stopping the server");
			server.stop();
		}
	}

	protected abstract Properties getNetworkProperties();

}
