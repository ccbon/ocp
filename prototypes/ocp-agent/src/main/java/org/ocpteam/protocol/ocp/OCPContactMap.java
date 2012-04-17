package org.ocpteam.protocol.ocp;

import java.util.Iterator;

import org.ocpteam.component.Agent;
import org.ocpteam.component.ContactMap;
import org.ocpteam.entity.Contact;
import org.ocpteam.misc.Id;
import org.ocpteam.misc.JLG;

public class OCPContactMap extends ContactMap {
	
	
	
	@Override
	public Contact add(Contact c) throws Exception {
		super.add(c);
		OCPAgent agent = (OCPAgent) ds().getComponent(Agent.class);
		OCPContact contact = (OCPContact) c;
		if (contact.nodeIdSet.size() == 0) {
			throw new Exception("contact without node.");
		}
		Iterator<Id> it = contact.nodeIdSet.iterator();
		while (it.hasNext()) {
			Id id = it.next();
			JLG.debug("adding node to nodeMap");
			agent.nodeMap.put(id, contact);
		}
		return c;
	}
	
	@Override
	public void addMyself() throws Exception {
		super.addMyself();
		OCPAgent agent = (OCPAgent) ds().getComponent(Agent.class);
		OCPContact contact = (OCPContact) ds().toContact();
		if (contact.nodeIdSet.size() == 0) {
			throw new Exception("contact without node.");
		}
		Iterator<Id> it = contact.nodeIdSet.iterator();
		while (it.hasNext()) {
			Id id = it.next();
			JLG.debug("adding node to nodeMap");
			agent.nodeMap.put(id, contact);
		}
	}
}
