package org.ocpteam.entity;

import java.io.Serializable;

import org.ocpteam.misc.Id;

public class Node implements Serializable {

	private Id nodeId;
	private int ring;
	
	public Node(Id nodeId) {
		this.setNodeId(nodeId);
	}

	public Node(Id nodeId, int ring) {
		this(nodeId);
		this.setRing(ring);
	}

	public Id getNodeId() {
		return nodeId;
	}

	public void setNodeId(Id nodeId) {
		this.nodeId = nodeId;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	public String toString() {
		return "node:" + nodeId.toString();
	}

	public int getRing() {
		return ring;
	}

	public void setRing(int ring) {
		this.ring = ring;
	}

}
