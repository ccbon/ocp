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
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.URL;

public class ContactMap extends DataSourceContainer {

	private Map<String, Contact> map;

	public ContactMap() {
		map = Collections.synchronizedMap(new HashMap<String, Contact>());
	}

	@Override
	public DSPDataSource ds() {
		// TODO Auto-generated method stub
		return (DSPDataSource) super.ds();
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
		DSPModule m = ds().client.getProtocol().getComponent(DSPModule.class);
		byte[] message = ds().client.getProtocol().getMessageSerializer()
				.serializeInput(new InputMessage(m.ping()));
		ds().client.sendAll(message);
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

	public Contact put(String name, Contact contact) {
		JLG.debug("adding contact to contactmap: " + contact);
		if (contact.getName().equals(ds().getName())) {
			// myself is already added by addMyself...
			return contact;
		}
		return map.put(name, contact);
	}

	public Contact remove(String name) {
		return map.remove(name);
	}
	
	public Contact remove(Contact contact) {
		return map.remove(contact.getName());
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public boolean containsValue(Contact contact) {
		return map.containsValue(contact);
	}

	public Contact add(Contact c) throws Exception {
		return this.put(c.getName(), c);
	}

	public void addMyself() throws Exception {
		Contact myself = ds().getComponent(Agent.class).toContact();
		myself.getUrlList().clear();
		myself.setMyself(true);
		try {
			myself.getUrlList().add(new URL("myself://localhost:0"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		map.put(myself.getName(), myself);
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
		Contact[] result = new Contact[map.size() - 1];
		int i = 0;
		for (Contact c : (Contact[]) map.values().toArray(new Contact[map.size()])) {
			if (c.isMyself()) {
				continue;
			}
			result[i] = c;
			i++;
		}
		return result;
	}

	public void removeAll() {
		map.clear();
	}

	public Collection<Contact> values() {
		return map.values();
	}

}
