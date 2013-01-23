package org.ocpteamx.protocol.ocp;

import java.util.Iterator;

import org.ocpteam.component.ContactMap;
import org.ocpteam.misc.Id;
import org.ocpteam.misc.LOG;
import org.ocpteam.serializable.Contact;

public class OCPContactMap extends ContactMap {

	@Override
	public Contact add(Contact c) throws Exception {
		super.add(c);
		OCPAgent agent = ds().getComponent(OCPAgent.class);
		OCPContact contact = (OCPContact) c;
		if (contact.nodeIdSet.size() == 0) {
			throw new Exception("contact without node.");
		}
		Iterator<Id> it = contact.nodeIdSet.iterator();
		while (it.hasNext()) {
			Id id = it.next();
			LOG.debug("adding node to nodeMap");
			agent.nodeMap.put(id, contact);
		}
		return c;
	}

	@Override
	public void addMyself() throws Exception {
		super.addMyself();
		OCPAgent agent = ds().getComponent(OCPAgent.class);
		OCPContact contact = (OCPContact) ds().toContact();
		if (contact.nodeIdSet.size() == 0) {
			throw new Exception("contact without node.");
		}
		Iterator<Id> it = contact.nodeIdSet.iterator();
		while (it.hasNext()) {
			Id id = it.next();
			LOG.debug("adding node to nodeMap");
			agent.nodeMap.put(id, contact);
		}
	}
}
