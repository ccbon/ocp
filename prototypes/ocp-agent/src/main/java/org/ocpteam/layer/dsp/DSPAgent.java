package org.ocpteam.layer.dsp;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;

import org.ocpteam.layer.rsp.Agent;
import org.ocpteam.layer.rsp.DataSource;
import org.ocpteam.misc.Id;
import org.ocpteam.misc.JLG;

/**
 * Provide an agent class evolving in a distributed environment.
 * All the contact features are defined here.
 *
 */
public abstract class DSPAgent extends Agent {

	public Properties cfg;
	
	protected Map<Id, Contact> contactMap; // contactid->contact
	
	public DSPAgent(DataSource ds) {
		super(ds);
		cfg = this.ds.getProperties();
		contactMap = new HashMap<Id, Contact>();
	}
	
	public boolean isFirstAgent() {
		if (cfg == null) {
			JLG.debug("p is null");
		}
		String s = cfg.getProperty("server", "no");
		return s.equalsIgnoreCase("yes")
				&& cfg.getProperty("server.isFirstAgent", "no").equalsIgnoreCase(
						"yes");
	}
	
	public abstract void refreshContactList() throws Exception;

	public List<Contact> getContactSnapshotList() {
		// we return a snapshot and not the modifiable contact list
		return new LinkedList<Contact>(contactMap.values());

	}

	public void addContact(Contact contact) throws Exception {
		contactMap.put(contact.id, contact);
	}

	public Contact removeContact(Contact contact) {
		return contactMap.remove(contact.id);
	}

	public Contact getContact(Id contactId) throws Exception {
		Contact contact = contactMap.get(contactId);
		if (contact == null) {
			throw new Exception("contact not found in my contact list.");
		}
		return contact;
	}

	public boolean hasNoContact() {
		return contactMap.size() == 0;
	}

	public boolean hasContact(Contact contact) {
		return contactMap.containsValue(contact);
	}

	public abstract Queue<Contact> makeContactQueue() throws Exception;

	public abstract Contact toContact();

	public abstract boolean hasStorage();

	public abstract void removeStorage();

}
