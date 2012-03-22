package org.ocpteam.component;

import java.util.Iterator;
import java.util.Properties;

import org.ocpteam.core.IComponent;
import org.ocpteam.interfaces.IDataModel;
import org.ocpteam.interfaces.IListener;
import org.ocpteam.interfaces.IProtocol;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.URL;

public abstract class DSPDataSource extends DataSource {

	protected Agent agent;
	protected Client client;
	protected Server server;
	
	protected Properties network;
	public IProtocol protocol;
	
	
	public DSPDataSource() throws Exception {
		agent = addComponent(Agent.class);
		client = addComponent(Client.class);
		server = addComponent(Server.class);
		
		protocol = addComponent(IProtocol.class, new BahBahProtocol());
		
		addComponent(TCPListener.class).setProtocol(protocol);
		
		
		addComponent(IDataModel.class, new MapDataModel());
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
		Iterator<IComponent> it = iteratorComponent();
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
