package org.ocpteam.protocol.ocp;

import java.io.ByteArrayInputStream;
import java.net.ConnectException;
import java.util.ArrayList;
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
import org.ocpteam.component.Agent;
import org.ocpteam.component.Authentication;
import org.ocpteam.component.Client;
import org.ocpteam.component.ContactMap;
import org.ocpteam.component.DataModel;
import org.ocpteam.layer.dsp.Contact;
import org.ocpteam.layer.rsp.Authenticable;
import org.ocpteam.layer.rsp.Context;
import org.ocpteam.layer.rsp.User;
import org.ocpteam.misc.Id;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.JLGException;
import org.ocpteam.misc.URL;

public class OCPClient extends Client implements Authenticable {

	private OCPAgent agent;
	private List<Channel> understandableChannelList;
	private Map<URL, Channel> channelMap;

	public OCPClient() {
		understandableChannelList = new ArrayList<Channel>();
		understandableChannelList.add(new TCPChannel());
		understandableChannelList.add(new MyselfChannel());
		channelMap = new HashMap<URL, Channel>();
	}

	private boolean understand(Channel c) {
		Iterator<Channel> it = understandableChannelList.iterator();
		while (it.hasNext()) {
			Channel uc = it.next();
			if (uc.getClass().equals(c.getClass())) {
				return true;
			}
		}
		return false;
	}

	public OCPContact getContact(Channel channel) throws Exception {
		// I have to request to an agent (sending to it a string and then
		// receiving a response
		// For that, I need to know the channel to use.
		JLG.debug("get contact from channel " + channel);
		if (understand(channel)) {
			OCPContact c = channel.getContact();
			return c;
		}
		JLG.warn("channel not reachable. get contact returns null.");
		return null;
	}

	public Properties getNetworkProperties() throws JLGException {
		try {
			Response response = request(Protocol
					.message(Protocol.NETWORK_PROPERTIES));
			Properties network = new Properties();
			// network.loadFromXML(new
			// ByteArrayInputStream(response.getBytes()));
			network.load(new ByteArrayInputStream(response.getBytes()));
			return network;
		} catch (Exception e) {
			throw new JLGException(e);
		}
	}

	public Id[] requestNodeId() throws Exception {
		Id[] nodeIds = null;
		// at this time we ask to the network to give us one node_id.

		Response response = request(Protocol.message(Protocol.NODE_ID));
		nodeIds = new Id[1];
		nodeIds[0] = new Id(response.getBytes());
		return nodeIds;
	}

	public Response request(byte[] string) throws Exception {
		ContactMap contactMap = agent.ds.getDesigner().get(ContactMap.class);
		if (contactMap.isEmpty()) {
			findSponsor();
		}
		Response response = request(agent.makeContactQueue(), string);
		if (response == null) {
			throw new NoNetworkException();
		}

		return response;
	}

	private Response request(Queue<Contact> contactQueue, byte[] input)
			throws Exception {
		byte[] output = null;
		ContactMap contactMap = agent.ds.getDesigner().get(ContactMap.class);
		if (contactMap.isEmpty()) {
			findSponsor();
		}
		OCPContact contact = null;
		while ((!contactQueue.isEmpty()) && (output == null)) {
			contact = (OCPContact) contactQueue.poll();
			try {
				output = request(contact, input);
			} catch (DetachedAgentException e) {
				detach(contact);
			}
		}
		if (output == null) {
			throw new NoNetworkException();
		}

		return new Response(output, contact);
	}

	private void findSponsor() throws Exception {

		// find some contact from your sponsor or die alone...
		Iterator<String> it = getPotentialSponsorIterator();
		while (it.hasNext()) {
			String sUrl = it.next();
			URL url = new URL(sUrl);
			Channel channel = Channel.getInstance(url, agent);
			OCPContact sponsor = getContact(channel);
			if (sponsor != null) {
				JLG.debug("we found a pingable sponsor channel");
				agent.addContact(sponsor);
			} else {
				JLG.warn("channel not pingable: " + channel);
			}
		}
		ContactMap contactMap = agent.ds.getDesigner().get(ContactMap.class);
		if (contactMap.isEmpty()) {
			throw new Exception("no pingable sponsor found.");
		}
	}

	private Iterator<String> getPotentialSponsorIterator() throws Exception {
		List<String> list = new LinkedList<String>();
		if (agent.cfg.getProperty("network.type", "private").equalsIgnoreCase(
				"public")) {
			try {
				XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
				// TODO: need a ocp dedicated web server. I use mine for the
				// time being.
				config.setServerURL(new java.net.URL(agent.cfg.getProperty(
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
			Iterator<String> it = agent.cfg.stringPropertyNames().iterator();
			while (it.hasNext()) {
				String key = it.next();
				if (key.startsWith("sponsor.")) {
					list.add(agent.cfg.getProperty(key));
				}
			}
			if (list.isEmpty()) {
				throw new Exception(
						"no sponsor provided in agent property file.");
			}
		}

		return list.iterator();
	}

	public void enrichContact(OCPContact contact) throws Exception {
		byte[] response = request(contact, Protocol.GET_CONTACT.getBytes());
		OCPContact c = (OCPContact) JLG.deserialize(response);
		String host = contact.urlList.iterator().next().getHost();
		c.updateHost(host);
		contact.copy(c);
	}

	private void detach(Contact contact) throws Exception {
		// tell to your contacts this contact has disappeared.
		synchronized (agent) {
			ContactMap contactMap = agent.ds.getDesigner()
					.get(ContactMap.class);
			if (!contactMap.containsValue(contact)) {
				return;
			}
			agent.removeContact(contact);
			sendAll(Protocol.hasBeenDetached((OCPContact) contact));
		}
	}

	public void sendAll(byte[] message) throws Exception {
		// tell all your contact of what happened

		Set<Contact> contactToBeDetached = new HashSet<Contact>();
		ContactMap contactMap = agent.ds.getDesigner().get(ContactMap.class);
		Iterator<Contact> itc = contactMap.getContactSnapshotList().iterator();
		while (itc.hasNext()) {
			Contact c = itc.next();
			if (c.id.equals(agent.id)) {
				// do not send the message to myself
				continue;
			}
			try {
				// we do not care about the response
				send(c, message);
			} catch (DetachedAgentException e) {
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

	public void send(Contact c, byte[] message) throws Exception {
		byte[] response = request(c, message);
		Response r = new Response(response, (OCPContact) c);
		if (!r.isSuccess()) {
			throw new Exception("error while informing: " + response);
		}
	}

	private byte[] request(Contact contact, byte[] string) throws Exception {
		OCPContact ocpContact = (OCPContact) contact;
		byte[] output = null;
		// I have to request to an agent (sending to it a string and then
		// receiving a response
		// For that, I need to know the channel to use.
		// for the time being I know only the TCP channel.
		Iterator<URL> it = ocpContact.urlList.iterator();
		while (it.hasNext()) {
			URL url = it.next();

			Channel channel = null;
			if (channelMap.containsKey(url)) {
				channel = channelMap.get(url);
			} else {
				channel = Channel.getInstance(url, agent);
				channelMap.put(url, channel);
			}
			if (understand(channel)) {
				try {
					output = channel.request(string);

				} catch (ConnectException e) {
					continue;
				} catch (Exception e) {
					JLG.warn(e);
				}
				return output;
			}
		}
		throw new DetachedAgentException();
	}

	public Captcha askCaptcha(Queue<Contact> contactQueue) throws Exception {
		Response r = request(contactQueue, Protocol.GENERATE_CAPTCHA.getBytes());
		Captcha captcha = (Captcha) JLG.deserialize(r.getBytes());
		JLG.debug("captcha content = " + captcha);
		// if (!captcha.checkSignature(r.getContact())) {
		// throw new Exception("captcha signature not consistant");
		// }

		return captcha;
	}

	public void createUser(Contact contact, ObjectData data, Link link,
			Captcha captcha, String answer) throws Exception {
		byte[] request = Protocol.message(Protocol.CREATE_USER, data, link,
				captcha, answer);
		send(contact, request);
	}

	public void store(Queue<Contact> contactQueue, Address address,
			Content content) throws Exception {
		// store an object at given address and put the content.
		byte[] request = Protocol.message(Protocol.CREATE_OBJECT,
				address.getBytes(), content);
		Response response = request(contactQueue, request);
		if (!response.isSuccess()) {
			throw new Exception("cannot store");
		}
	}

	public byte[] getUser(Id key) throws Exception {
		Response r = request(agent.makeContactQueue(key),
				Protocol.message(Protocol.GET_USER, key.getBytes()));
		r.checkForError();
		return r.getBytes();
	}

	public Content getFromAddress(Address address) throws Exception {
		Response r = request(agent.makeContactQueue(address),
				Protocol.message(Protocol.GET_ADDRESS, address.getBytes()));
		r.checkForError();
		if (new String(r.getBytes()).equals(new String(
				Protocol.ADDRESS_NOT_FOUND))) {
			return null;
		}
		return (Content) JLG.deserialize(r.getBytes());
	}

	public void remove(Address address, byte[] addressSignature)
			throws Exception {
		Response r = request(agent.makeContactQueue(address), Protocol.message(
				Protocol.REMOVE_ADDRESS, address, addressSignature));
		r.checkForError();
	}

	public void declareSponsor() {
		try {
			if (agent.cfg.getProperty("network.type", "public")
					.equalsIgnoreCase("public")) {
				int port = agent.toContact().urlList.get(0).getPort();
				XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
				// TODO: need a ocp dedicated web server. I use mine for the
				// time being.
				config.setServerURL(new java.net.URL(agent.cfg.getProperty(
						"network.sponsor.url",
						OCPAgent.DEFAULT_SPONSOR_SERVER_URL)));
				XmlRpcClient client = new XmlRpcClient();
				client.setConfig(config);
				String result = (String) client.execute("add",
						new Object[] { "" + port });
				JLG.debug("result = " + result);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void login() throws Exception {
		try {
			Authentication a = ds.getDesigner().get(Authentication.class);
			String password = (String) a.getChallenge();
			String login = a.getLogin();
			Id key = agent.hash(agent.ucrypt(password,
					(login + password).getBytes()));
			byte[] content = null;
			try {
				content = getUser(key);
			} catch (Exception e) {
			}
			if (content == null) {
				throw new Exception("user unknown");
			}
			User user = (User) JLG.deserialize(agent
					.udecrypt(password, content));
			if (user == null) {
				throw new Exception("user unknown");
			}
			DataModel dm = new OCPFileSystem((OCPUser) user, agent);
			ds.setContext(new Context(dm, "/"));
			a.setUser(user);
		} catch (Exception e) {
			JLG.error(e);
			throw e;
		}
	}

	@Override
	public void logout() throws Exception {
		JLG.debug("ocp logout (nothing to do).");
	}

	public void setAgent(OCPAgent agent) {
		this.agent = agent;
	}
	
	public OCPAgent getAgent() {
		if (agent == null) {
			agent = (OCPAgent) ds.getDesigner().get(Agent.class);
		}
		return agent;
	}

	@Override
	public void connect() throws Exception {
		getAgent().connect();
	}

	

	@Override
	public void disconnect() throws Exception {
		getAgent().disconnect();
	}

}
