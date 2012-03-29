package org.ocpteam.component;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.ocpteam.entity.Contact;
import org.ocpteam.entity.InputMessage;
import org.ocpteam.misc.Id;
import org.ocpteam.misc.URL;
import org.ocpteam.module.DSPModule;

public class ContactMap extends DataSourceContainer {

	private Map<Id, Contact> map;

	public ContactMap() {
		map = Collections.synchronizedMap(new HashMap<Id, Contact>());
	}

	@Override
	public DSPDataSource ds() {
		// TODO Auto-generated method stub
		return (DSPDataSource) super.ds();
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
		DSPModule m = ds().getComponent(DSPModule.class);
		byte[] message = ds().client.getProtocol().getMessageSerializer()
				.serializeInput(new InputMessage(m.ping()));
		ds().client.sendAll(message);
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

	public void add(Contact c) throws Exception {
		this.put(c.getId(), c);
	}

	public void addMyself() throws Exception {
		Contact myself = ds().getComponent(Agent.class).toContact();
		myself.getUrlList().clear();
		try {
			myself.getUrlList().add(new URL("myself://localhost:0"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		myself.setMyself(true);
		this.add(myself);
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

	public Contact[] getArray() {
		return (Contact[]) map.values().toArray(new Contact[map.size()]);
	}

}
