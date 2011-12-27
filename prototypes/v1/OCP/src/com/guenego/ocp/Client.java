package com.guenego.ocp;

import java.io.ByteArrayInputStream;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.Set;

import com.guenego.misc.Id;
import com.guenego.misc.JLG;
import com.guenego.misc.JLGException;
import com.guenego.misc.URL;

public class Client {

	private Agent agent;
	private List<Channel> understandableChannelList;
	private Map<URL, Channel> channelMap;

	public Client(Agent _agent) {
		agent = _agent;
		understandableChannelList = new ArrayList<Channel>();
		understandableChannelList.add(new TCPChannel());
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

	public Id ping(Channel channel) throws Exception {
		// I have to request to an agent (sending to it a string and then
		// receiving a response
		// For that, I need to know the channel to use.
		JLG.debug("pinging channel " + channel);
		if (understand(channel)) {
			return channel.ping();
		}
		JLG.warn("channel not pingable. ping return null.");
		return null;
	}

	public Properties getNetworkProperties() throws JLGException {
		try {
			Response response = request(Protocol.NETWORK_PROPERTIES);
			Properties network = new Properties();
			// network.loadFromXML(new
			// ByteArrayInputStream(response.getBytes()));
			network.load(new ByteArrayInputStream(response.getString()
					.getBytes()));
			return network;
		} catch (Exception e) {
			throw new JLGException(e);
		}
	}

	public Id[] requestNodeId() throws Exception {
		Id[] nodeIds = null;
		// at this time we ask to the network to give us one node_id.

		Response response = request(Protocol.NODE_ID);
		nodeIds = new Id[1];
		nodeIds[0] = new Id(response.getString());
		return nodeIds;
	}

	public Response request(String string) throws Exception {
		if (agent.hasNoContact()) {
			findSponsor();
		}
		Response response = request(agent.makeContactQueue(), string);
		if (response == null) {
			throw new NoNetworkException();
		}

		return response;
	}

	private Response request(Queue<Contact> contactQueue, String sRequest)
			throws Exception {
		String sResponse = null;
		if (agent.hasNoContact()) {
			findSponsor();
		}
		Contact contact = null;
		while ((!contactQueue.isEmpty()) && (sResponse == null)) {
			contact = contactQueue.poll();
			try {
				sResponse = request(contact, sRequest);
			} catch (DetachedAgentException e) {
				detach(contact);
			}
		}
		if (sResponse == null) {
			throw new NoNetworkException();
		}

		return new Response(sResponse, contact);
	}

	private void findSponsor() throws Exception {
		Contact sponsor = null;
		// find some contact from your sponsor or die alone...
		Iterator<String> it = agent.p.stringPropertyNames().iterator();
		boolean bSponsorInProperty = false;
		while (it.hasNext()) {
			String key = it.next();
			if (key.startsWith("sponsor.")) {
				bSponsorInProperty = true;
				String sUrl = agent.p.getProperty(key);
				URL url = new URL(sUrl);
				Channel channel = Channel.getInstance(url);
				Id idc = ping(channel);
				if (idc != null) {
					JLG.debug("we found a pingable sponsor channel");
					sponsor = new Contact(idc);
					sponsor.addURL(url);
					getInfo(sponsor);
					agent.addContact(sponsor);

				} else {
					JLG.warn("channel not pingable: " + channel);
				}
			}
		}
		if (bSponsorInProperty == false) {
			throw new Exception("sponsor not specified in agent property");
		}
		if (agent.hasNoContact()) {
			throw new Exception("no pingable sponsor found.");
		}
	}

	public void getInfo(Contact contact) throws Exception {
		String response = request(contact, Protocol.GET_CONTACT);
		Contact c = (Contact) JLG.deserialize(response);
		contact.copy(c);
	}

	private void detach(Contact contact) throws Exception {
		// tell to your contacts this contact has disappeared.
		synchronized (agent) {
			if (!agent.hasContact(contact)) {
				return;
			}
			agent.removeContact(contact);
			sendAll(Protocol.hasBeenDetached(contact));
		}
	}

	public void sendAll(String message) throws Exception {
		// tell all your contact of what happened
		synchronized (agent) {
			Set<Contact> contactToBeDetached = new HashSet<Contact>();
			Iterator<Contact> itc = agent.getContactIterator();
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
				}
			}
			Iterator<Contact> it = contactToBeDetached.iterator();
			while (it.hasNext()) {
				Contact c = it.next();
				detach(c);
			}
		}
	}

	public void send(Contact c, String message) throws Exception {
		String response = request(c, message);
		if (!response.equals(Protocol.SUCCESS)) {
			throw new Exception("error while informing: " + response);
		}
	}

	private String request(Contact contact, String string) throws Exception {
		String response = null;
		// I have to request to an agent (sending to it a string and then
		// receiving a response
		// For that, I need to know the channel to use.
		// for the time being I know only the TCP channel.
		Iterator<URL> it = contact.urlList.iterator();
		while (it.hasNext()) {
			URL url = it.next();
			
			Channel channel = null;
			if (channelMap.containsKey(url)) {
				channel = channelMap.get(url);
			} else {
				channel = Channel.getInstance(url);
				channelMap.put(url, channel);
			}
			if (understand(channel)) {
				try {
					response = channel.request(string);
				} catch (ConnectException e) {
					continue;
				}
				return response;
			}
		}
		throw new DetachedAgentException();
	}

	public Captcha askCaptcha(Queue<Contact> contactQueue) throws Exception {
		Response r = request(contactQueue, Protocol.GENERATE_CAPTCHA);
		Captcha captcha = (Captcha) JLG.deserialize(r.getString());
		JLG.debug("captcha content = " + captcha);
		// if (!captcha.checkSignature(r.getContact())) {
		// throw new Exception("captcha signature not consistant");
		// }

		return captcha;
	}

	public void createUser(Contact contact, ObjectData data, Link link,
			Captcha captcha, String answer) throws Exception {
		String request = Protocol.message(Protocol.CREATE_USER, data, link, captcha,
				answer);
		send(contact, request);
	}

	public void store(Queue<Contact> contactQueue, Address address,
			Content content) throws Exception {
		// store an object at given address and put the content.
		String request = Protocol.message(Protocol.CREATE_OBJECT,
				address.getBytes(), content);
		Response response = request(contactQueue, request);
		if (!response.getString().equals(Protocol.SUCCESS)) {
			throw new Exception("cannot store");
		}
	}

	public byte[] getUser(Id key) throws Exception {
		Response r = request(agent.makeContactQueue(key),
				Protocol.message(Protocol.GET_USER, key.getBytes()));
		r.checkForError();
		return JLG.hexToBytes(r.getString());
	}

	public Content getFromAddress(Address address) throws Exception {
		Response r = request(agent.makeContactQueue(address),
				Protocol.message(Protocol.GET_ADDRESS, address.getBytes()));
		r.checkForError();
		if (r.getString().equals(Protocol.ADDRESS_NOT_FOUND)) {
			return null;
		}
		return (Content) JLG.deserialize(r.getString());
	}

	public void remove(Address address, byte[] addressSignature)
			throws Exception {
		Response r = request(agent.makeContactQueue(address), Protocol.message(
				Protocol.REMOVE_ADDRESS, address, addressSignature));
		r.checkForError();
	}

}
