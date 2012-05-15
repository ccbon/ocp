package org.ocpteam.component;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NavigableMap;
import java.util.Queue;
import java.util.TreeMap;

import org.ocpteam.interfaces.INodeMap;
import org.ocpteam.misc.Id;
import org.ocpteam.misc.JLG;
import org.ocpteam.serializable.Address;
import org.ocpteam.serializable.Contact;
import org.ocpteam.serializable.Node;

public class NodeMap extends DSContainer<DSPDataSource> implements INodeMap {
	private NavigableMap<Id, Contact> nodeMap;

	@Override
	public void init() throws Exception {
		super.init();
		nodeMap = (NavigableMap<Id, Contact>) new TreeMap<Id, Contact>();
	}

	private Contact getContact(Id nodeId) {
		return nodeMap.get(nodeId);
	}

	@Override
	public void put(Node node, Contact c) throws Exception {
		if (nodeMap == null) {
			throw new Exception("nodeMap is null");
		}
		nodeMap.put(node.getNodeId(), c);
	}

	@Override
	public void remove(Node node) {
		nodeMap.remove(node.getNodeId());
	}

	public NavigableMap<Id, Contact> getNodeMap() {
		return nodeMap;
	}

	@Override
	public void removeAll() {
		nodeMap.clear();
	}

	@Override
	public boolean isResponsible(Address address) throws Exception {
		Node node = ds().getNode();
		if (node == null) {
			return false;
		}
		return getNode(address).equals(node.getNodeId());
	}

	private Id getNode(Id address) throws Exception {
		Id nodeId = nodeMap.floorKey(address);
		if (nodeId == null) {
			try {
				nodeId = nodeMap.lastKey();
			} catch (Exception e) {
				if (nodeMap.isEmpty()) {
					throw new Exception("nodeMap is not populated at all");
				} else {
					throw e;
				}
			}
		}
		JLG.debug("nodeId=" + nodeId);
		JLG.debug("agent=" + nodeMap.get(nodeId));
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

	@Override
	public Contact getPredecessor(Node node) throws Exception {
		Id nodeId = node.getNodeId();

		Id previousNodeId = nodeMap.lowerKey(nodeId);
		if (previousNodeId == null) {
			previousNodeId = nodeMap.lastKey();
		}
		if (previousNodeId == null) {
			throw new Exception(
					"cannot find predecessor. It seems that the nodeMap is empty.");
		}
		return getContact(previousNodeId);

	}

	@Override
	public Contact getSuccessor(Node node) throws Exception {
		Id nodeId = node.getNodeId();

		Id nextNodeId = nodeMap.higherKey(nodeId);
		if (nextNodeId == null) {
			nextNodeId = nodeMap.firstKey();
		}
		if (nextNodeId == null) {
			throw new Exception(
					"cannot find successor. It seems that the nodeMap is empty.");
		}
		return getContact(nextNodeId);
	}

	public int size() {
		return nodeMap.size();
	}

	public boolean isEmpty() {
		return nodeMap.isEmpty();
	}

	@Override
	public String toString() {
		return nodeMap.toString();
	}

	@Override
	public void askForNode() throws Exception {
		MessageDigest md = ds().getComponent(MessageDigest.class);
		Random random = ds().getComponent(Random.class);
		ds().setNode(new Node(new Id(md.hash(random.generate()))));
	}
}
