package org.ocpteam.component;

import java.net.URI;
import java.util.Properties;

import org.ocpteam.interfaces.IProtocol;
import org.ocpteam.misc.JLG;

public abstract class DSPDataSource extends DataSource {

	public Agent agent;
	public Client client;
	protected Server server;

	public Properties network;
	public IProtocol protocol;
	public TCPListener tcplistener;
	public UDPListener udplistener;
	public ContactMap contactMap;

	public DSPDataSource() throws Exception {
		addComponent(Agent.class);
		addComponent(Client.class);
		addComponent(Server.class);
		addComponent(Protocol.class);
		addComponent(TCPListener.class);
		addComponent(UDPListener.class);
		addComponent(ContactMap.class);
		addComponent(DSPModule.class);
	}

	@Override
	public void init() throws Exception {
		super.init();
		agent = getComponent(Agent.class);
		client = getComponent(Client.class);
		server = getComponent(Server.class);
		protocol = getComponent(Protocol.class);
		tcplistener = getComponent(TCPListener.class);
		tcplistener.setProtocol(protocol);
		udplistener = getComponent(UDPListener.class);
		udplistener.setProtocol(protocol);
		contactMap = getComponent(ContactMap.class);
	}

	@Override
	public synchronized void connect() throws Exception {
		super.connect();
		if (agent.isFirstAgent()) {
			JLG.debug("This is the first agent on the network");
			network = this.getNetworkProperties();
		} else {
			network = client.getNetworkProperties();
		}
		JLG.debug("network properties: " + JLG.propertiesToString(network));
		readNetworkConfig();
		
		client.askForContact();

		if (getProperty("server", "yes").equals("yes")) {
			JLG.debug("starting the server");
			configureServer(server);
			server.start();
			askForNode();
			contactMap.addMyself();
			client.declareContact();
			onNodeArrival();
		}
	}

	/**
	 * transfer data from network to this node.
	 * @throws Exception 
	 */
	protected void onNodeArrival() throws Exception {
	}

	/**
	 * Request and set node 
	 * @throws Exception 
	 */
	protected void askForNode() throws Exception {
	}

	protected void readNetworkConfig() throws Exception {

	}

	protected void configureServer(Server server) throws Exception {
		int port = Integer.parseInt(getProperty("server.port", "22222"));

		tcplistener.setUrl(new URI("tcp://localhost:" + port));
		udplistener.setUrl(new URI("udp://localhost:" + port));

		server.getListeners().clear();
		server.getListeners().add(tcplistener);
		server.getListeners().add(udplistener);
	}

	@Override
	public synchronized void disconnect() throws Exception {
		super.disconnect();
		if (server.isStarted()) {
			onNodeNiceDeparture();
			JLG.debug("stopping the server");
			server.stop();
		}
	}

	protected void onNodeNiceDeparture() throws Exception {		
	}

	protected Properties getNetworkProperties() {
		return JLG.extractProperties(getConfig(), "network");
	}


}
