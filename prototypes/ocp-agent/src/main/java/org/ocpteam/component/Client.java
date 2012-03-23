package org.ocpteam.component;

import java.net.ConnectException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.ocpteam.core.IComponent;
import org.ocpteam.entity.Contact;
import org.ocpteam.exception.NotAvailableContactException;
import org.ocpteam.interfaces.IClient;
import org.ocpteam.interfaces.IProtocol;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.URL;
import org.ocpteam.protocol.ocp.OCPContact;
import org.ocpteam.protocol.ocp.OCPProtocol;
import org.ocpteam.protocol.ocp.Response;
import org.ocpteam.transaction.DeclareContactTransaction;

public class Client extends DataSourceContainer implements IClient {

	private Map<URL, Channel> channelMap;
	protected HashMap<String, Class<? extends Channel>> channelFactoryMap;

	public Client() throws Exception {
		addComponent(TCPChannel.class);
		addComponent(MyselfChannel.class);
	}

	@Override
	public void init() {
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
		return new Properties();
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
		byte[] input = getProtocol().getTransactionSerializer().serialize(
				DeclareContactTransaction.class, contact);
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
		sendAll(OCPProtocol.hasBeenDetached((OCPContact) contact));
	}

	public void send(Contact c, byte[] message) throws Exception {
		byte[] response = request(c, message);
		Response r = new Response(response, (OCPContact) c);
		if (!r.isSuccess()) {
			throw new Exception("error while informing: " + response);
		}
	}

	private IProtocol getProtocol() {
		return ds().getComponent(Protocol.class);
	}

	private Agent getAgent() {
		return ds().getComponent(Agent.class);
	}

}
