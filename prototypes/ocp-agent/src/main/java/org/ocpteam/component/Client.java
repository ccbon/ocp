package org.ocpteam.component;

import java.net.SocketException;
import java.net.URI;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.ocpteam.entity.Contact;
import org.ocpteam.entity.InputMessage;
import org.ocpteam.entity.Response;
import org.ocpteam.exception.NoNetworkException;
import org.ocpteam.exception.NotAvailableContactException;
import org.ocpteam.interfaces.IClient;
import org.ocpteam.interfaces.IProtocol;
import org.ocpteam.misc.JLG;
import org.ocpteam.network.TCPClient;
import org.ocpteam.network.UDPClient;
import org.ocpteam.protocol.ocp.OCPAgent;

public class Client extends DataSourceContainer implements IClient {

	private ContactMap contactMap;
	private ExecutorService executor;

	public Client() throws Exception {
	}

	@Override
	public void init() throws Exception {
		super.init();
		contactMap = ds().getComponent(ContactMap.class);
	}

	/**
	 * @return the network properties coming from a server (or a peer)
	 */
	public Properties getNetworkProperties() throws Exception {
		DSPModule m = getProtocol().getComponent(DSPModule.class);
		JLG.debug("module class: " + m.getClass());
		byte[] input = getProtocol().getMessageSerializer().serializeInput(
				new InputMessage(m.getNetworkProperties()));
		Response response = request(input);
		Properties network = (Properties) getProtocol().getMessageSerializer()
				.deserializeOutput(response.getBytes());
		return network;
	}

	private void findSponsor() throws Exception {
		// find some contact from your sponsor or die alone...
		Iterator<String> it = getPotentialSponsorIterator();
		while (it.hasNext()) {
			String sUrl = it.next();
			URI url = new URI(sUrl);

			Contact sponsor = getContact(url);
			if (sponsor != null) {
				JLG.debug("we found a pingable sponsor");
				contactMap.add(sponsor);
			} else {
				JLG.warn("url not pingable: " + url);
			}
		}

		if (contactMap.isEmpty()) {
			throw new Exception("no pingable sponsor found.");
		}
	}

	private Iterator<String> getPotentialSponsorIterator() throws Exception {
		List<String> list = new LinkedList<String>();
		if (ds().getProperty("network.type", "private").equalsIgnoreCase(
				"public")) {
			try {
				XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
				// TODO: need a ocp dedicated web server. I use mine for the
				// time being.
				config.setServerURL(new java.net.URL(ds().getProperty(
						"network.sponsor.url",
						OCPAgent.DEFAULT_SPONSOR_SERVER_URL)));
				XmlRpcClient client = new XmlRpcClient();
				client.setConfig(config);
				Object[] result = (Object[]) client.execute("list",
						new Object[] {});
				for (int i = 0; i < result.length; i++) {
					@SuppressWarnings("unchecked")
					Map<String, String> map = (Map<String, String>) result[i];
					JLG.debug("url = " + map.get("url"));
					list.add(map.get("url"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (list.isEmpty()) {
				JLG.warn("first agent on the public network");
			}

		} else { // private network... agent properties must have at least one
					// sponsor specified.
			Iterator<String> it = ds().iterator();
			while (it.hasNext()) {
				String key = it.next();
				if (key.startsWith("sponsor.")) {
					list.add(ds().getProperty(key));
				}
			}
			if (list.isEmpty()) {
				throw new Exception(
						"no sponsor provided in agent property file. Suggestion: add someting like properties sponsor.1=tcp://localhost:12345");
			}
		}

		return list.iterator();
	}

	public Contact getContact(URI url) throws Exception {
		JLG.debug("getContact");
		try {
			TCPClient tcpClient = new TCPClient(url.getHost(), url.getPort(),
					getProtocol());
			DSPModule m = getProtocol().getComponent(DSPModule.class);
			byte[] input = getProtocol().getMessageSerializer().serializeInput(
					new InputMessage(m.getContact()));
			byte[] response = tcpClient.request(input);
			Contact c = (Contact) getProtocol().getMessageSerializer()
					.deserializeOutput(response);
			// we update a host because an agent does not see its public
			// address.
			c.setHost(url.getHost());
			tcpClient.releaseSocket();
			return c;
		} catch (Exception e) {
			JLG.warn("contact not reachable. get contact returns null.");
			return null;
		}
	}

	public Response request(byte[] string) throws Exception {
		if (contactMap.isEmpty()) {
			findSponsor();
		}
		Response response = request(contactMap.makeContactQueue(), string);
		if (response == null) {
			throw new NoNetworkException();
		}
		return response;
	}

	public Response request(Queue<Contact> contactQueue, byte[] input)
			throws Exception {
		byte[] output = null;
		if (contactMap.isEmpty()) {
			findSponsor();
		}
		JLG.debug("contact queue size: " + contactQueue.size());
		Contact contact = null;
		while ((!contactQueue.isEmpty()) && (output == null)) {
			contact = (Contact) contactQueue.poll();
			JLG.debug("contact: " + contact);
			try {
				output = request(contact, input);
			} catch (NotAvailableContactException e) {
			}
		}
		if (output == null) {
			throw new NoNetworkException();
		}

		return new Response(output, contact);
	}

	public byte[] request(Contact contact, byte[] input) throws Exception {
		JLG.debug("sending request on contact: " + contact);
		byte[] output = null;
		// use the TCP connection.
		TCPClient tcpClient = contactMap.getTcpClient(contact);

		try {
			output = tcpClient.request(input);
			return output;
		} catch (java.net.SocketTimeoutException e) {
			JLG.debug("SocketTimeoutException on contact " + contact);

		} catch (java.net.ConnectException e) {
			JLG.debug("ConnectException on contact " + contact);

		} catch (SocketException e) {
			JLG.debug("SocketException on contact " + contact);

		}

		JLG.debug("about to throw a not available exception regarding contact "
				+ contact);
		detach(contact);
		throw new NotAvailableContactException();
	}

	public void declareContact() throws Exception {
		Contact contact = getAgent().toContact();
		DSPModule m = getProtocol().getComponent(DSPModule.class);
		byte[] input = getProtocol().getMessageSerializer().serializeInput(
				new InputMessage(m.declareContact(), contact));
		JLG.debug(ds().getName() + " declares contact: " + contact);
		sendAll(input);
	}

	public void sendAll(final byte[] message) throws Exception {
		JLG.debug("send all");
		Collection<Callable<Object>> tasks = new LinkedList<Callable<Object>>();
		for (final Contact c : contactMap.getOtherContacts()) {
			tasks.add(Executors.callable(new Runnable() {
				@Override
				public void run() {
					try {
						// we do not care about the response
						send(c, message);
					} catch (NotAvailableContactException e) {
						JLG.debug("detach");
						try {
							detach(c);
						} catch (Exception e1) {
							e.printStackTrace();
							// at least we tried...
						}
					} catch (Exception e) {
						// we don't care for agent that don't understand the
						// sent
						// message.
						JLG.debug("Contact answered with error: "
								+ e.getMessage());
					}
				}
			}));
		}
		try {
			executor.invokeAll(tasks);
		} catch (NullPointerException e) {
		} catch (InterruptedException e) {
		}
	}

	public void detach(Contact contact) throws Exception {
		JLG.debug("detaching contact: " + contact);

		contactMap.remove(contact);
		// tell to your contacts this contact has disappeared.
		// this code is comment it for performance reasons...
		// DSPModule m = getProtocol().getComponent(DSPModule.class);
		// final byte[] message =
		// getProtocol().getMessageSerializer().serializeInput(
		// new InputMessage(m.detach(), contact));
		//
		// exe.execute(new Runnable() {
		//
		// @Override
		// public void run() {
		// try {
		// sendAll(message);
		// } catch (Exception e) {
		// }
		// }
		// });
	}

	public void sendQuick(Contact c, byte[] message) throws Exception {
		JLG.debug(ds().getName() + " sends a message to " + c);

		UDPClient udpClient = contactMap.getUdpClient(c);
		if (udpClient == null) {
			try {
				request(c, message);
			} catch (Exception e) {
			}
			return;
		}

		try {
			udpClient.send(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void send(Contact c, byte[] message) throws Exception {
		JLG.debug(ds().getName() + " sends a message to " + c);
		try {
			request(c, message);
		} catch (NotAvailableContactException e) {
		}
		return;
	}

	public IProtocol getProtocol() {
		return ds().getComponent(Protocol.class);
	}

	public Agent getAgent() {
		return ds().getComponent(Agent.class);
	}

	public void askForContact() throws Exception {
		DSPModule m = getProtocol().getComponent(DSPModule.class);
		byte[] input = getProtocol().getMessageSerializer().serializeInput(
				new InputMessage(m.askForContact()));
		for (Contact c : contactMap.getOtherContacts()) {
			try {
				JLG.debug(ds().getName() + " requests askForContact");
				byte[] response = request(c, input);
				// strategy : friends of my friends are my friends.
				Contact[] contactsOfContact = (Contact[]) getProtocol()
						.getMessageSerializer().deserializeOutput(response);
				JLG.debug(ds().getName() + " received "
						+ contactsOfContact.length + " contact(s).");
				for (Contact nc : contactsOfContact) {
					JLG.debug("nc: " + nc);
					if (nc.getName().equals(ds().getName())) {
						JLG.debug("skipping");
						continue;
					}
					contactMap.add(nc);
				}
			} catch (NotAvailableContactException e) {
			}
		}
	}

	public void sendAllAsync(final byte[] message) {
		executor.execute(new Runnable() {

			@Override
			public void run() {
				try {
					sendAll(message);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void start() throws Exception {
		executor = Executors.newCachedThreadPool();

	}

	@Override
	public void stop() throws Exception {
		if (executor != null) {
			executor.shutdown();
			executor.shutdownNow();
			executor = null;
		}
	}

	@Override
	public boolean isStarted() {
		return executor != null;
	}

	public ExecutorService getExecutor() {
		return executor;
	}
}
