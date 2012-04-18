package org.ocpteam.protocol.dht1;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.ocpteam.component.DSPDataSource;
import org.ocpteam.component.NodeMap;
import org.ocpteam.entity.Context;
import org.ocpteam.entity.Node;
import org.ocpteam.interfaces.IDataModel;
import org.ocpteam.misc.Id;
import org.ocpteam.misc.JLG;

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
	private MessageDigest md;
	protected NodeMap nodeMap;

	public DHT1DataSource() throws Exception {
		super();
		addComponent(NodeMap.class);
		addComponent(IDataModel.class, new DHT1DataModel());
		addComponent(DHT1Module.class);
	}

	@Override
	public void init() throws Exception {
		super.init();
		map = Collections.synchronizedMap(new HashMap<String, String>());
		dm = (DHT1DataModel) getComponent(IDataModel.class);
		nodeMap = getComponent(NodeMap.class);
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
		node = new Node(hash(random()));
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
}
