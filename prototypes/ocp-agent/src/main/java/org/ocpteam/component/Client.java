package org.ocpteam.component;

import java.io.Serializable;
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
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.ocpteam.entity.Response;
import org.ocpteam.exception.NoNetworkException;
import org.ocpteam.exception.NotAvailableContactException;
import org.ocpteam.interfaces.IClient;
import org.ocpteam.interfaces.IProtocol;
import org.ocpteam.interfaces.IStartable;
import org.ocpteam.misc.LOG;
import org.ocpteam.network.TCPClient;
import org.ocpteam.network.UDPClient;
import org.ocpteam.serializable.Contact;
import org.ocpteam.serializable.ContactList;
import org.ocpteam.serializable.InputMessage;
import org.ocpteamx.protocol.ocp.OCPAgent;

public class Client extends DSContainer<DSPDataSource> implements IClient, IStartable {

	private ContactMap contactMap;
	private ExecutorService executor;
	private CompletionService<Object> completionService;
	private int remainingTasks;

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
		DSPModule m = ds().getComponent(DSPModule.class);
		LOG.info("module class: " + m.getClass());
		Response response = request(new InputMessage(m.getNetworkProperties()));
		Properties network = (Properties) response.getObject();
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
				LOG.info("we found a pingable sponsor");
				contactMap.add(sponsor);
			} else {
				LOG.warn("url not pingable: " + url);
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
					LOG.info("url = " + map.get("url"));
					list.add(map.get("url"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (list.isEmpty()) {
				LOG.warn("first agent on the public network");
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
		LOG.info("getContact");
		try {
			TCPClient tcpClient = new TCPClient(url.getHost(), url.getPort(),
					getProtocol());
			DSPModule m = ds().getComponent(DSPModule.class);
			Contact c = (Contact) tcpClient.request(new InputMessage(m.getContact()));
			// we update a host because an agent does not see its public
			// address.
			c.setHost(url.getHost());
			tcpClient.releaseSocket();
			return c;
		} catch (Exception e) {
			LOG.warn("contact not reachable. get contact returns null.");
			return null;
		}
	}

	public Response request(Serializable string) throws Exception {
		if (contactMap.isEmpty()) {
			findSponsor();
		}
		Response response = requestByPriority(contactMap.makeContactQueue(), string);
		if (response == null) {
			throw new NoNetworkException();
		}
		return response;
	}

	public Response requestByPriority(Queue<Contact> contactQueue, Serializable input)
			throws Exception {
		Serializable output = null;
		LOG.info("contact queue size: " + contactQueue.size());
		Contact contact = null;
		boolean done = false;
		while (!contactQueue.isEmpty()) {
			contact = contactQueue.poll();
			LOG.info("contact: " + contact);
			try {
				output = request(contact, input);
				done = true;
				break;
			} catch (NotAvailableContactException e) {
			} catch (Exception e) {
				//unexpected but not fatal...
				e.printStackTrace();
			}
		}
		if (done == false) {
			throw new NoNetworkException();
		}

		return new Response(output, contact);
	}

	public Serializable request(Contact contact, Serializable input)
			throws Exception {
		if (input instanceof InputMessage) {
			InputMessage im = (InputMessage) input;
			LOG.info(ds().getName() + " sending message (" + im.transaction.getName() + ") on contact: " + contact);
		} else {
			LOG.info("sending request on contact: " + contact);
		}
		Serializable output = null;
		// use the TCP connection.
		TCPClient tcpClient = contactMap.getTcpClient(contact);

		try {
			output = tcpClient.request(input);
			return output;
		} catch (java.net.SocketTimeoutException e) {
			LOG.info("SocketTimeoutException on contact " + contact);

		} catch (java.net.ConnectException e) {
			LOG.info("ConnectException on contact " + contact);

		} catch (SocketException e) {
			LOG.info("SocketException on contact " + contact);

		}

		LOG.info("about to throw a not available exception regarding contact "
				+ contact);
		detach(contact);
		throw new NotAvailableContactException();
	}

	public void declareContact() throws Exception {
		Contact contact = ds().toContact();
		DSPModule m = ds().getComponent(DSPModule.class);
		LOG.info(ds().getName() + " declares contact: " + contact);
		sendAll(new InputMessage(m.declareContact(), contact));
		waitForCompletion();
	}

	public void sendAll(final Serializable message) throws Exception {
		LOG.info("send all");
		Collection<Callable<Object>> tasks = new LinkedList<Callable<Object>>();
		for (final Contact c : contactMap.getOtherContacts()) {
			tasks.add(Executors.callable(new Runnable() {
				@Override
				public void run() {
					try {
						// we do not care about the response
						send(c, message);
					} catch (NotAvailableContactException e) {
						LOG.info("detach");
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
						LOG.info("Contact answered with error: "
								+ e.getMessage());
					}
				}
			}));
		}
		for (Callable<Object> task : tasks) {
			try {
				completionService.submit(task);
				remainingTasks++;
				LOG.info("increasing remainingTasks = " + remainingTasks);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		LOG.info("last remainingTasks = " + remainingTasks);
	}

	public void detach(Contact contact) throws Exception {
		LOG.info("detaching contact: " + contact);
		
		contactMap.remove(contact);
		ds().onDetach(contact);
		// tell to your contacts this contact has disappeared.
		// this code is comment it for performance reasons...
		// DSPModule m = ds().getComponent(DSPModule.class);
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
		LOG.info(ds().getName() + " sends a message to " + c);

		UDPClient udpClient = contactMap.getUdpClient(c);
		if (udpClient == null) {
			LOG.info("no udp client found");
			try {
				request(c, message);
			} catch (Exception e) {
			}
			return;
		}

		try {
			LOG.info("udp client found");
			udpClient.send(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void send(Contact c, Serializable message) throws Exception {
		LOG.info("send");
		try {
			request(c, message);
		} catch (NotAvailableContactException e) {
		}
		return;
	}

	public IProtocol getProtocol() {
		return ds().getComponent(Protocol.class);
	}

	public void askForContact() throws Exception {
		DSPModule m = ds().getComponent(DSPModule.class);
		
		
		for (Contact c : contactMap.getOtherContacts()) {
			try {
				LOG.info(ds().getName() + " requests askForContact");
				ContactList contactsOfContact = (ContactList) request(c,
						new InputMessage(m.askForContact()));
				// strategy : friends of my friends are my friends.
				LOG.info(ds().getName() + " received "
						+ contactsOfContact.getList().size() + " contact(s).");
				for (Contact nc : contactsOfContact.getList()) {
					LOG.info("nc: " + nc);
					if (nc.getName().equals(ds().getName())) {
						LOG.info("skipping");
						continue;
					}
					contactMap.add(nc);
				}
			} catch (NotAvailableContactException e) {
			}
		}
	}

	@Override
	public void start() throws Exception {
		executor = Executors.newCachedThreadPool();
		completionService = new ExecutorCompletionService<Object>(executor);
		remainingTasks = 0;
		LOG.info("client started");
	}

	@Override
	public void stop() throws Exception {
		if (executor != null) {
			executor.shutdown();
			executor.shutdownNow();
			executor = null;
		}
		completionService = null;
	}

	@Override
	public boolean isStarted() {
		return executor != null;
	}

	public ExecutorService getExecutor() {
		return executor;
	}

	public void waitForCompletion() {
		try {
			LOG.info("remainingTasks = " + remainingTasks);
			while (remainingTasks > 0) {
				LOG.info("remainingTasks = " + remainingTasks);
				completionService.take();
				remainingTasks--;
			}
		} catch (InterruptedException e) {
			remainingTasks = 0;
		}
	}


}
