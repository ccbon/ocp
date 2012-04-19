package org.ocpteam.component;

import org.ocpteam.entity.Contact;
import org.ocpteam.entity.Node;
import org.ocpteam.interfaces.INodeMap;
import org.ocpteam.misc.Id;

/**
 * A node map with many rings. A ring is instanciated by a nodeMap.
 *
 */
public class RingNodeMap extends DataSourceContainer implements INodeMap {

	private int ringNbr;
	
	private NodeMap[] rings;
	
	@Override
	public void put(Node node, Contact c) {
		int r = node.getRing();
		rings[r].put(node, c);	
	}

	@Override
	public void remove(Node node) {
		int r = node.getRing();
		rings[r].remove(node);	
	}

	@Override
	public void removeAll() {
		for (NodeMap nodeMap : rings) {
			nodeMap.removeAll();
		}
	}

	@Override
	public Contact getPredecessor() throws Exception {
		Node node = ds().getNode();
		int r = node.getRing();
		return rings[r].getPredecessor();
	}

	@Override
	public boolean isResponsible(Id address) throws Exception {
		Node node = ds().getNode();
		int r = node.getRing();
		return rings[r].isResponsible(address);
	}

	public int getRingNbr() {
		return ringNbr;
	}

	public void setRingNbr(int ringNbr) throws Exception {
		this.ringNbr = ringNbr;
		rings = new NodeMap[ringNbr];
		for (int i = 0; i < ringNbr; i++) {
			NodeMap nodeMap = new NodeMap();
			nodeMap.setParent(this.getParent());
			nodeMap.init();
			rings[i] = nodeMap;
		}
	}


}
