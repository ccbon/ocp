package org.ocpteam.component;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.ocpteam.entity.Contact;
import org.ocpteam.misc.Id;
import org.ocpteam.protocol.ocp.OCPAgent;
import org.ocpteam.protocol.ocp.Protocol;

public class ContactMap extends DataSourceContainer {

	private HashMap<Id, Contact> map;
	
	public ContactMap() {
		map = new HashMap<Id, Contact>();
	}

	public Contact getContact(Id contactId) throws Exception {
		Contact contact = map.get(contactId);
		if (contact == null) {
			throw new Exception("contact not found in my contact list.");
		}
		return contact;
	}
	
	public void refreshContactList() throws Exception {
		// TODO: make independant of ocp by adding the P2P client functionality.
		OCPAgent agent = (OCPAgent) ds().getComponent(Agent.class);
		agent.client.sendAll(Protocol.PING.getBytes());
	}
	
	public List<Contact> getContactSnapshotList() {
		// we return a snapshot and not the modifiable contact list
		return new LinkedList<Contact>(map.values());
	}

	public Set<Id> keySet() {
		return map.keySet();
	}

	public Contact get(Id id) {
		return map.get(id);
	}

	public Contact put(Id id, Contact contact) {
		return map.put(id, contact);
	}

	public Contact remove(Id id) {
		return map.remove(id);
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public boolean containsValue(Contact contact) {
		return map.containsValue(contact);
	}
	
	

}
