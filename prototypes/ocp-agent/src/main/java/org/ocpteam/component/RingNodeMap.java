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
public class RingNodeMap extends DSContainer<DataSource> implements INodeMap {

	private int ringNbr;

	private Map<Integer, NodeMap> rings;

	@Override
	public void put(Node node, Contact c) throws Exception {
		int r = node.getRing();
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
		JLG.debug("ringNodeMap contains " + getRoot().getName());
		JLG.debug("ringNodeMap " + this);
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
	public Contact getPredecessor(Node node) throws Exception {
		int r = node.getRing();
		return rings.get(r).getPredecessor(node);
	}
	
	@Override
	public Contact getSuccessor(Node node) throws Exception {
		int r = node.getRing();
		return rings.get(r).getSuccessor(node);
	}

	@Override
	public boolean isResponsible(Id address) throws Exception {
		Node node = ds().getNode();
		if (node == null) {
			return false;
		}
		int r = node.getRing();
		return rings.get(r).isResponsible(address);
	}

	public int getRingNbr() {
		return ringNbr;
	}

	public void setRingNbr(int ringNbr) throws Exception {
		this.ringNbr = ringNbr;
		if (rings == null) {
			rings = new HashMap<Integer, NodeMap>();
		}
		for (int i = 0; i < ringNbr; i++) {
			if (!rings.containsKey(i)) {
				NodeMap nodeMap = new NodeMap();
				nodeMap.setParent(this.getParent());
				nodeMap.init();
				rings.put(i, nodeMap);
			}
		}
	}

	public NodeMap[] getNodeMaps() {
		return rings.values().toArray(new NodeMap[rings.size()]);
	}

	public int getLessPopulatedRing() {
		int size = rings.get(0).size();
		JLG.debug("ring.size[0]=" + size);
		int result = 0;
		for (int i = 1; i < rings.size(); i++) {
			int newSize = Math.min(size, rings.get(i).size());
			JLG.debug("ring.size[" + i + "]=" + rings.get(i).size());
			if (newSize < size) {
				result = i;
				size = newSize;
			}
		}
		JLG.debug("lessPopulatedRing=" + result);
		return result;
	}

	@Override
	public String toString() {
		String result = "rings: " + rings.toString() + JLG.NL;
		for (int i = 0; i < rings.size(); i++) {
			NodeMap nodeMap = rings.get(i);
			result += nodeMap.toString() + JLG.NL;
		}
		return result;
	}


}
