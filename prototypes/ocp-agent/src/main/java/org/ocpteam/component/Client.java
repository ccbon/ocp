package org.ocpteam.component;

import java.net.ConnectException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.ocpteam.core.IComponent;
import org.ocpteam.entity.Contact;
import org.ocpteam.interfaces.IClient;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.URL;
import org.ocpteam.protocol.ocp.DetachedAgentException;
import org.ocpteam.protocol.ocp.MyselfChannel;
import org.ocpteam.protocol.ocp.TCPChannel;

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
		byte[] output = null;
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
				channel = channelFactoryMap.get(url.getProtocol().toLowerCase()).newInstance();
				channel.setUrl(url);
				channel.setParent(this.getParent());
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

	public boolean understand(Channel c) {
		return usesComponent(c.getClass());
	}

}
