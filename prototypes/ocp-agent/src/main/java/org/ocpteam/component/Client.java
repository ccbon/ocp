package org.ocpteam.component;

import java.net.ConnectException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.Set;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.ocpteam.core.IComponent;
import org.ocpteam.entity.Contact;
import org.ocpteam.entity.InputMessage;
import org.ocpteam.entity.Response;
import org.ocpteam.exception.NoNetworkException;
import org.ocpteam.exception.NotAvailableContactException;
import org.ocpteam.interfaces.IClient;
import org.ocpteam.interfaces.IProtocol;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.URL;
import org.ocpteam.module.DSPModule;
import org.ocpteam.protocol.ocp.OCPAgent;

public class Client extends DataSourceContainer implements IClient {

	private Map<URL, Channel> channelMap;
	protected HashMap<String, Class<? extends Channel>> channelFactoryMap;

	public Client() throws Exception {
		addComponent(TCPChannel.class);
		addComponent(MyselfChannel.class);
	}

	@Override
	public void init() throws Exception {
		super.init();
		channelMap = new HashMap<URL, Channel>();
		initFactory();
	}

	public Channel newChannel(URL url) throws Exception {
		Class<? extends Channel> c = channelFactoryMap.get(url.getProtocol()
				.toLowerCase());
		if (c == null) {
			c = UnknownChannel.class;
		}
		Channel channel = c.newInstance();
		channel.setUrl(url);
		channel.setParent(this.getParent());
		return channel;
	}

	private void initFactory() {
		channelFactoryMap = new HashMap<String, Class<? extends Channel>>();
		Iterator<IComponent> it = iteratorComponent();
		while (it.hasNext()) {
			IComponent c = it.next();
			if (c instanceof Channel) {
				String protocol = ((Channel) c).getProtocolName().toLowerCase();
				channelFactoryMap.put(protocol, ((Channel) c).getClass());
			}
		}
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
		ContactMap contactMap = ds().getComponent(ContactMap.class);
		// find some contact from your sponsor or die alone...
		Iterator<String> it = getPotentialSponsorIterator();
		while (it.hasNext()) {
			String sUrl = it.next();
			URL url = new URL(sUrl);
			Channel channel = newChannel(url);

			Contact sponsor = getContact(channel);
			if (sponsor != null) {
				JLG.debug("we found a pingable sponsor channel");
				contactMap.add(sponsor);
			} else {
				JLG.warn("channel not pingable: " + channel);
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

	public Contact getContact(Channel channel) throws Exception {
		// I have to request to an agent (sending to it a string and then
		// receiving a response
		// For that, I need to know the channel to use.
		JLG.debug("get contact from channel " + channel);
		if (understand(channel)) {
			Contact c = channel.getContact();
			return c;
		}
		JLG.warn("channel not reachable. get contact returns null.");
		return null;
	}

	public Response request(byte[] string) throws Exception {
		ContactMap contactMap = ds().getComponent(ContactMap.class);
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
		ContactMap contactMap = ds().getComponent(ContactMap.class);
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
				detach(contact);
			}
		}
		if (output == null) {
			throw new NoNetworkException();
		}

		return new Response(output, contact);
	}

	public byte[] request(Contact contact, byte[] string) throws Exception {
		JLG.debug("sending request on contact: " + contact);
		byte[] output = null;
		// I have to request to an agent (sending to it a string and then
		// receiving a response
		// For that, I need to know the channel to use.
		// for the time being I know only the TCP channel.
		Iterator<URL> it = contact.getUrlList().iterator();
		while (it.hasNext()) {
			URL url = it.next();

			Channel channel = null;
			if (channelMap.containsKey(url)) {
				channel = channelMap.get(url);
			} else {
				channel = newChannel(url);
				channelMap.put(url, channel);
			}
			if (understand(channel)) {
				try {
					JLG.debug("sending request with channel: " + channel);
					output = channel.request(string);
				} catch (ConnectException e) {
					continue;
				} catch (Exception e) {
					JLG.warn(e);
				}
				return output;
			}
		}
		throw new NotAvailableContactException();
	}

	public boolean understand(Channel c) {
		return usesComponent(c.getClass());
	}

	public void declareContact() throws Exception {
		Contact contact = getAgent().toContact();
		JLG.debug("declare contact: " + contact);
		DSPModule m = getProtocol().getComponent(DSPModule.class);
		JLG.debug("getMessageSerializer="
				+ getProtocol().getMessageSerializer());
		JLG.debug("DSPModule=" + m);
		byte[] input = getProtocol().getMessageSerializer().serializeInput(
				new InputMessage(m.declareContact(), contact));
		sendAll(input);
	}

	public void sendAll(byte[] message) throws Exception {
		// tell all your contact of what happened

		Set<Contact> contactToBeDetached = new HashSet<Contact>();
		ContactMap contactMap = ds().getComponent(ContactMap.class);
		Iterator<Contact> itc = contactMap.getContactSnapshotList().iterator();
		while (itc.hasNext()) {
			Contact c = itc.next();
			if (c.isMyself()) {
				// do not send the message to myself
				continue;
			}
			try {
				// we do not care about the response
				send(c, message);
			} catch (NotAvailableContactException e) {
				contactToBeDetached.add(c);
			} catch (Exception e) {
				// we don't care for agent that don't understand the sent
				// message.
				JLG.debug("Contact answered with error: " + e.getMessage());
			}
		}
		Iterator<Contact> it = contactToBeDetached.iterator();
		while (it.hasNext()) {
			Contact c = it.next();
			detach(c);
		}

	}

	public void detach(Contact contact) throws Exception {
		JLG.debug("detaching contact: " + contact);
		// tell to your contacts this contact has disappeared.

		ContactMap contactMap = ds().getComponent(ContactMap.class);
		if (!contactMap.containsValue(contact)) {
			return;
		}
		contactMap.remove(contact.getId());
		DSPModule m = getProtocol().getComponent(DSPModule.class);
		byte[] message = getProtocol().getMessageSerializer().serializeInput(
				new InputMessage(m.detach(), contact));
		sendAll(message);
	}

	public void send(Contact c, byte[] message) throws Exception {
		request(c, message);
	}

	public IProtocol getProtocol() {
		return ds().getComponent(Protocol.class);
	}

	public Agent getAgent() {
		return ds().getComponent(Agent.class);
	}

}
