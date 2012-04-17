package org.ocpteam.protocol.dht1;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;

import org.ocpteam.component.ContactMap;
import org.ocpteam.component.DSPDataSource;
import org.ocpteam.entity.Contact;
import org.ocpteam.entity.Context;
import org.ocpteam.entity.Node;
import org.ocpteam.interfaces.IDataModel;
import org.ocpteam.misc.Id;
import org.ocpteam.misc.JLG;
import org.ocpteam.protocol.ocp.OCPContact;

/**
 * DHT is a distributed hashtable, with no redundancy and no node detachment
 * management.
 * 
 * Strategies: - Each agent is responsible for a specific territory specified by
 * a nodeId The responsibility is from node_id to succ(node_id). - node_id is
 * chosen in a random way.
 * 
 * Potentials issues: - Loss of data when an agent disappears or disconnects.
 * 
 */
public class DHT1DataSource extends DSPDataSource {

	private Map<String, String> map;
	private DHT1DataModel dm;
	private Id nodeId;
	private MessageDigest md;
	protected DHT1ContactMap contactMap;

	public DHT1DataSource() throws Exception {
		super();
		replaceComponent(ContactMap.class, new DHT1ContactMap());
		addComponent(IDataModel.class, new DHT1DataModel());
		addComponent(DHT1Module.class);
	}

	@Override
	public void init() throws Exception {
		super.init();
		map = Collections.synchronizedMap(new HashMap<String, String>());
		dm = (DHT1DataModel) getComponent(IDataModel.class);
		nodeId = null;
		contactMap = (DHT1ContactMap) getComponent(ContactMap.class);
	}

	@Override
	public String getProtocolName() {
		return "DHT1";
	}

	@Override
	public synchronized void connect() throws Exception {
		super.connect();
		Context c = new Context(dm);
		setContext(c);
	}

	@Override
	protected void readNetworkConfig() throws Exception {
		super.readNetworkConfig();
		md = MessageDigest.getInstance(network.getProperty("hash", "SHA-1"));
		nodeId = hash(random());
	}

	private byte[] random() {
		SecureRandom random = new SecureRandom();
		byte bytes[] = new byte[20];
		random.nextBytes(bytes);
		return bytes;
	}

	public Id hash(byte[] input) throws Exception {
		return new Id(md.digest(input));
	}

	public void store(String key, String value) {
		JLG.debug("local store: " + key + "->" + value);
		map.put(key, value);
	}

	public String retrieve(String key) {
		JLG.debug("local retrieve: " + key);
		return map.get(key);
	}

	public void destroy(String key) {
		map.remove(key);
	}

	public Set<String> keySet() {
		return map.keySet();
	}

	@Override
	public Contact toContact() throws Exception {
		Contact c = super.toContact();
		c.setDomain(new Node(nodeId));
		return c;
	}

	public boolean isResponsible(String key) throws Exception {
		Id id = getNodeId(key);
		return id.equals(nodeId);
	}

	private Id getNodeId(String key) throws Exception {
		Id hash = hash(key.getBytes());
		Id nodeId = contactMap.getNodeMap().floorKey(hash);
		if (nodeId == null) {
			nodeId = contactMap.getNodeMap().lastKey();
		}
		if (nodeId == null) {
			throw new Exception("nodeMap is not populated at all");
		}
		return nodeId;
	}

	public Queue<Contact> getContactQueue(String skey) throws Exception {
		Id key = hash(skey.getBytes());
		Queue<Contact> contactQueue = new LinkedList<Contact>();
		NavigableMap<Id, Contact> nodeMap = new TreeMap<Id, Contact>(
				contactMap.getNodeMap());
		if (nodeMap.containsKey(key)) {
			contactQueue.offer(nodeMap.get(key));
		}

		NavigableMap<Id, Contact> s = nodeMap.headMap(key, false);
		Iterator<Id> it = s.navigableKeySet().descendingIterator();
		while (it.hasNext()) {
			Id nodeId = it.next();
			Contact contact = s.get(nodeId);
			if (!contactQueue.contains(contact)) {
				contactQueue.offer(contact);
			}
		}
		s = nodeMap.tailMap(key, false);
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
