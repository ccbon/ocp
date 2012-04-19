package org.ocpteam.component;

import java.util.HashMap;
import java.util.Map;

import org.ocpteam.entity.Contact;
import org.ocpteam.entity.Node;
import org.ocpteam.interfaces.INodeMap;
import org.ocpteam.misc.Id;
import org.ocpteam.misc.JLG;

/**
 * A node map with many rings. A ring is instanciated by a nodeMap.
 *
 */
public class RingNodeMap extends DataSourceContainer implements INodeMap {

	private int ringNbr;
	
	private Map<Integer, NodeMap> rings;
	
	@Override
	public void put(Node node, Contact c) throws Exception {
		int r = node.getRing();
		JLG.debug("r=" + r);
		JLG.debug("rings=" + rings);
		if (rings == null) {
			rings = new HashMap<Integer, NodeMap>();
		}
		if (!rings.containsKey(r)) {
			NodeMap nodeMap = new NodeMap();
			nodeMap.setParent(this.getParent());
			nodeMap.init();
			rings.put(r, nodeMap);
		}
		rings.get(r).put(node, c);	
	}

	@Override
	public void remove(Node node) {
		int r = node.getRing();
		rings.get(r).remove(node);	
	}

	@Override
	public void removeAll() {
		for (NodeMap nodeMap : rings.values()) {
			nodeMap.removeAll();
		}
	}

	@Override
	public Contact getPredecessor() throws Exception {
		Node node = ds().getNode();
		int r = node.getRing();
		return rings.get(r).getPredecessor();
	}

	@Override
	public boolean isResponsible(Id address) throws Exception {
		Node node = ds().getNode();
		int r = node.getRing();
		return rings.get(r).isResponsible(address);
	}

	public int getRingNbr() {
		return ringNbr;
	}

	public void setRingNbr(int ringNbr) throws Exception {
		this.ringNbr = ringNbr;
		rings = new HashMap<Integer, NodeMap>();
		for (int i = 0; i < ringNbr; i++) {
			NodeMap nodeMap = new NodeMap();
			nodeMap.setParent(this.getParent());
			nodeMap.init();
			rings.put(i, nodeMap);
			JLG.debug("rings=" + rings);
		}
	}

	public NodeMap[] getNodeMaps() {
		return rings.values().toArray(new NodeMap[rings.size()]);
	}

	public int getLessPopulatedRing() {
		int size = rings.get(0).size();
		int result = 0;
		for (int i = 0; i < rings.size(); i++) {
			int newSize = Math.min(size, rings.get(i).size());
			if (newSize < size) {
				result = i;
				size = newSize;
			}
		}
		return result;
	}


}
