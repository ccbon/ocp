package org.ocpteam.functionality;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.ocpteam.design.Functionality;
import org.ocpteam.layer.dsp.Contact;
import org.ocpteam.layer.rsp.DataSource;
import org.ocpteam.misc.Id;
import org.ocpteam.protocol.ocp.OCPAgent;
import org.ocpteam.protocol.ocp.Protocol;

public class ContactMap extends HashMap<Id, Contact> implements Functionality<DataSource> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private DataSource parent;

	public Contact getContact(Id contactId) throws Exception {
		Contact contact = get(contactId);
		if (contact == null) {
			throw new Exception("contact not found in my contact list.");
		}
		return contact;
	}

	
	@Override
	public void setParent(DataSource parent) {
		this.parent = parent;
	}


	public void refreshContactList() throws Exception {
		// TODO: make independant of ocp by adding the P2P client functionality.
		OCPAgent agent = (OCPAgent) parent.getAgent();
		agent.client.sendAll(Protocol.PING.getBytes());
	}
	
	public List<Contact> getContactSnapshotList() {
		// we return a snapshot and not the modifiable contact list
		return new LinkedList<Contact>(values());
	}
	
	

}
