package org.ocpteam.component;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NavigableMap;
import java.util.Queue;
import java.util.TreeMap;

import org.ocpteam.entity.Contact;
import org.ocpteam.entity.Node;
import org.ocpteam.misc.Id;

public class NodeMap extends DataSourceContainer {
	private NavigableMap<Id, Contact> nodeMap;
	
	@Override
	public void init() throws Exception {
		super.init();
		nodeMap = (NavigableMap<Id, Contact>) new TreeMap<Id, Contact>();
	}
	
	public Contact get(Node node) {
		return nodeMap.get(node.getNodeId());
	}
	
	public void put(Node node, Contact c) {
		nodeMap.put(node.getNodeId(), c);		
	}

	public void remove(Node node) {
		nodeMap.remove(node.getNodeId());	
	}
	
	public NavigableMap<Id, Contact> getNodeMap() {
		return nodeMap;
	}

	public void removeAll() {
		nodeMap.clear();
	}
	
	public boolean isResponsible(Id address) throws Exception {
		return getNode(address).equals(ds().getNode().getNodeId());
	}

	private Id getNode(Id address) throws Exception {
		Id nodeId = nodeMap.floorKey(address);
		if (nodeId == null) {
			nodeId = nodeMap.lastKey();
		}
		if (nodeId == null) {
			throw new Exception("nodeMap is not populated at all");
		}
		return nodeId;
	}
	
	public Queue<Contact> getContactQueue(Id address) throws Exception {
		Queue<Contact> contactQueue = new LinkedList<Contact>();
		// take a snapshot of nodeMap.
		NavigableMap<Id, Contact> nodeMap = new TreeMap<Id, Contact>(
				this.nodeMap);
		if (nodeMap.containsKey(address)) {
			contactQueue.offer(nodeMap.get(address));
		}

		NavigableMap<Id, Contact> s = nodeMap.headMap(address, false);
		Iterator<Id> it = s.navigableKeySet().descendingIterator();
		while (it.hasNext()) {
			Id nodeId = it.next();
			Contact contact = s.get(nodeId);
			if (!contactQueue.contains(contact)) {
				contactQueue.offer(contact);
			}
		}
		s = nodeMap.tailMap(address, false);
		it = s.navigableKeySet().descendingIterator();
		while (it.hasNext()) {
			Id nodeId = it.next();
			Contact contact = s.get(nodeId);
			if (!contactQueue.contains(contact)) {
				contactQueue.offer(contact);
			}
		}
		return contactQueue;
	}



}
