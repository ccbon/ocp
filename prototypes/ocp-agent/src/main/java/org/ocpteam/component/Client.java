package org.ocpteam.component;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.ocpteam.interfaces.IClient;
import org.ocpteam.layer.dsp.Contact;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.URL;
import org.ocpteam.protocol.ocp.Channel;
import org.ocpteam.protocol.ocp.DetachedAgentException;
import org.ocpteam.protocol.ocp.MyselfChannel;
import org.ocpteam.protocol.ocp.TCPChannel;

public class Client extends DataSourceComponent implements IClient {

	private List<Channel> understandableChannelList;
	private Map<URL, Channel> channelMap;
	private Agent agent;
	
	public Client() {
		understandableChannelList = new ArrayList<Channel>();
		understandableChannelList.add(new TCPChannel());
		understandableChannelList.add(new MyselfChannel());
		channelMap = new HashMap<URL, Channel>();
	}
	
	public Agent getAgent() {
		if (agent == null) {
			agent = ds().getDesigner().get(Agent.class);
		}
		return agent;
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
				channel = Channel.getInstance(url, getAgent());
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
		Iterator<Channel> it = understandableChannelList.iterator();
		while (it.hasNext()) {
			Channel uc = it.next();
			if (uc.getClass().equals(c.getClass())) {
				return true;
			}
		}
		return false;
	}


}
