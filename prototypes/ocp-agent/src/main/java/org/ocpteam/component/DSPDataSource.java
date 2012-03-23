package org.ocpteam.component;

import java.util.Properties;

import org.ocpteam.interfaces.IProtocol;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.URL;

public abstract class DSPDataSource extends DataSource {

	protected Agent agent;
	protected Client client;
	protected Server server;

	public Properties network;
	public IProtocol protocol;
	private TCPListener listener;
	public ContactMap contactMap;

	public DSPDataSource() throws Exception {
		addComponent(Agent.class);
		addComponent(Client.class);
		addComponent(Server.class);
		addComponent(Protocol.class, new BahBahProtocol());
		addComponent(TCPListener.class);
		addComponent(ContactMap.class);
	}

	@Override
	public void init() {
		super.init();
		agent = getComponent(Agent.class);
		client = getComponent(Client.class);
		server = getComponent(Server.class);
		protocol = getComponent(Protocol.class);
		listener = getComponent(TCPListener.class);
		listener.setProtocol(protocol);
		contactMap = getComponent(ContactMap.class);
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
		readNetworkConfig();
		
		if (get("server", "yes").equals("yes")) {
			JLG.debug("starting the server");
			configureServer(server);
			server.start();
			contactMap.addMyself();
			client.declareContact();
		}
	}

	protected void readNetworkConfig() throws Exception {
		
	}

	protected void configureServer(Server server) throws Exception {
		listener.setUrl(new URL(
				get("listener.tcp.url", "tcp://localhost:22222")));
		server.getListeners().add(listener);
	}

	@Override
	public void disconnect() throws Exception {
		if (server.isStarted()) {
			JLG.debug("stopping the server");
			server.stop();
		}
	}

	protected Properties getNetworkProperties() {
		return JLG.extractProperties(getConfig(), "network");
	}

}
