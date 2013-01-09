package org.ocpteam.component;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.Properties;

import org.ocpteam.entity.Context;
import org.ocpteam.interfaces.IDataModel;
import org.ocpteam.interfaces.IProtocol;
import org.ocpteam.interfaces.ISerializer;
import org.ocpteam.interfaces.IUserManagement;
import org.ocpteam.misc.JLG;
import org.ocpteam.serializable.Contact;
import org.ocpteam.serializable.Node;

public abstract class DSPDataSource extends DataSource {

	private Node node;

	public Agent agent;
	public Client client;
	protected Server server;

	public Properties network;
	public IProtocol protocol;
	public TCPListener tcplistener;
	public UDPListener udplistener;
	public ContactMap contactMap;
	public ISerializer serializer;

	public DSPDataSource() throws Exception {
		addComponent(Agent.class);
		addComponent(Client.class);
		addComponent(Server.class);
		addComponent(Protocol.class);
		addComponent(TCPListener.class);
		addComponent(UDPListener.class);
		addComponent(ContactMap.class);
		addComponent(DSPModule.class);
		addComponent(ISerializer.class, new FListSerializer());
//		addComponent(ISerializer.class, new JavaSerializer());
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
		serializer = getComponent(ISerializer.class);
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
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
			configureServer();
			server.start();
			askForNode();
			contactMap.addMyself();
			client.declareContact();
			onNodeArrival();
		}

		if (usesComponent(IDataModel.class)) {
			
			if (!usesComponent(IUserManagement.class)) {
				// if no user management then attach the data model.
				setContext(new Context(getComponent(IDataModel.class)));
			}
		}
	}

	/**
	 * transfer data from network to this node.
	 * 
	 * @throws Exception
	 */
	protected void onNodeArrival() throws Exception {
	}

	/**
	 * Request and set node
	 * 
	 * @throws Exception
	 */
	protected void askForNode() throws Exception {
	}

	protected void readNetworkConfig() throws Exception {
		// TODO: Foreach component, if the component find a property for it,
		// then it has to take it.
		for (Object component : components()) {
			try {
				Method m = component.getClass().getMethod("readNetworkConfig");
				JLG.debug("invoke");
				m.invoke(component);
			} catch (NoSuchMethodException e) {
			}
		}
	}

	public void configureServer() throws Exception {
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

	public synchronized void disconnectHard() throws Exception {
		super.disconnect();
		if (server.isStarted()) {
			JLG.debug("stopping the server");
			server.stop();
		}
	}

	protected void onNodeNiceDeparture() throws Exception {
	}

	protected Properties getNetworkProperties() {
		return JLG.extractProperties(getConfig(), "network");
	}

	@Override
	public Contact toContact() throws Exception {
		Contact c = super.toContact();
		c.setNode(node);
		return c;
	}

}
