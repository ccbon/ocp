package org.ocpteam.component;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.ocpteam.entity.Contact;
import org.ocpteam.entity.InputMessage;
import org.ocpteam.exception.NotAvailableContactException;
import org.ocpteam.interfaces.INodeMap;
import org.ocpteam.misc.JLG;
import org.ocpteam.network.TCPClient;
import org.ocpteam.network.UDPClient;

public class ContactMap extends DSContainer<DSPDataSource> {

	private Map<String, Contact> map;
	private Map<String, TCPClient> tcpClientMap;
	private Map<String, UDPClient> udpClientMap;

	public ContactMap() {
		map = Collections.synchronizedMap(new HashMap<String, Contact>());
		tcpClientMap = Collections.synchronizedMap(new HashMap<String, TCPClient>());
		udpClientMap = Collections.synchronizedMap(new HashMap<String, UDPClient>());
	}

	public Contact getContact(String name) throws Exception {
		Contact contact = map.get(name);
		if (contact == null) {
			throw new Exception("contact not found in my contact list.");
		}
		return contact;
	}

	public void refreshContactList() throws Exception {
		// TODO: make independant of ocp by adding the P2P client functionality.
		DSPModule m = ds().getComponent(DSPModule.class);
		ds().client.sendAll(new InputMessage(m.ping()));
	}

	public List<Contact> getContactSnapshotList() {
		// we return a snapshot and not the modifiable contact list
		return new LinkedList<Contact>(map.values());
	}

	public Set<String> keySet() {
		return map.keySet();
	}

	public Contact get(String name) {
		return map.get(name);
	}

	public Contact remove(Contact contact) {
		if (ds().usesComponent(INodeMap.class)) {
			ds().getComponent(INodeMap.class).remove(contact.getNode());
		}
		String name = contact.getName();
		TCPClient tcpClient = tcpClientMap.get(name);
		if (tcpClient != null) {
			tcpClient.releaseSocket();
		}
		tcpClientMap.remove(name);
		udpClientMap.remove(name);
		return map.remove(name);
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public Contact add(Contact c) throws Exception {
		if (ds().usesComponent(INodeMap.class)) {
			ds().getComponent(INodeMap.class).put(c.getNode(), c);
		}
		return map.put(c.getName(), c);
	}

	public void addMyself() throws Exception {
		Contact myself = ds().toContact();
		myself.setMyself(true);
		add(myself);
	}

	public int size() {
		return map.size();
	}

	public Queue<Contact> makeContactQueue() throws Exception {
		return new LinkedList<Contact>(map.values());
	}

	@Override
	public String toString() {
		return map.toString();
	}

	public Contact[] getOtherContacts() {
		JLG.debug("contactmap=" + this);
		Contact[] result = null;
		if (containsMyself()) {
			result = new Contact[map.size() - 1];
		} else {
			result = new Contact[map.size()];
		}
		int i = 0;
		for (Contact c : map.values().toArray(new Contact[map.size()])) {
			if (c.isMyself()) {
				continue;
			}
			result[i] = c;
			i++;
		}
		return result;
	}

	public boolean containsMyself() {
		for (Contact c : map.values().toArray(new Contact[map.size()])) {
			if (c.isMyself()) {
				return true;
			}
		}
		return false;
	}

	public void removeAll() {
		if (ds().usesComponent(INodeMap.class)) {
			ds().getComponent(INodeMap.class).removeAll();
		}
		tcpClientMap.clear();
		udpClientMap.clear();
		map.clear();
	}

	public Collection<Contact> values() {
		return map.values();
	}

	public TCPClient getTcpClient(Contact contact) throws Exception {
		String name = contact.getName();
		contact = this.get(name);
		if (contact == null) {
			throw new NotAvailableContactException();
		}
		if (!tcpClientMap.containsKey(name)) {
			TCPClient tcpClient = new TCPClient(contact.getHost(), contact.getTcpPort(), ds().protocol);
			tcpClientMap.put(name, tcpClient);
		}
		return tcpClientMap.get(name);
	}

	public UDPClient getUdpClient(Contact contact) throws Exception {
		String name = contact.getName();
		JLG.debug("contact name: " + name);
		contact = this.get(name);
		if (contact.getUdpPort() <= 0) {
			return null;
		}
		if (!udpClientMap.containsKey(name)) {
			UDPClient udpClient = new UDPClient(contact.getHost(), contact.getUdpPort());
			udpClientMap.put(name, udpClient);
		}
		return udpClientMap.get(name);
	}

}
