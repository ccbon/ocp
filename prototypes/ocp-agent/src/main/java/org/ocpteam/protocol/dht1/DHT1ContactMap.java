package org.ocpteam.protocol.dht1;

import java.util.NavigableMap;
import java.util.TreeMap;

import org.ocpteam.component.ContactMap;
import org.ocpteam.entity.Contact;
import org.ocpteam.entity.Node;
import org.ocpteam.misc.Id;

public class DHT1ContactMap extends ContactMap {
	private NavigableMap<Id, Contact> nodeMap;

	@Override
	public void init() throws Exception {
		super.init();
		nodeMap = (NavigableMap<Id, Contact>) new TreeMap<Id, Contact>();
	}
	
	@Override
	public Contact add(Contact c) throws Exception {
		Contact contact = super.add(c);
		Node node = (Node) c.getDomain();
		nodeMap.put(node.getNodeId(), c);
		return contact;
	}
	
	
	@Override
	public Contact remove(Contact contact) {
		Contact c = super.remove(contact);
		Node node = (Node) c.getDomain();
		nodeMap.remove(node.getNodeId());
		return c;
	}
	
	@Override
	public void removeAll() {
		super.removeAll();
		nodeMap.clear();
	}
	
	public Contact getContactFromNodeId(Id id) {
		return nodeMap.get(id);
	}
	
	public NavigableMap<Id, Contact> getNodeMap() {
		return nodeMap;
	}
	
}
