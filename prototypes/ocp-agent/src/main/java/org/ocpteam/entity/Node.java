package org.ocpteam.entity;

import java.io.Serializable;

import org.ocpteam.misc.Id;

public class Node implements Serializable {

	private Id nodeId;

	public Node(Id nodeId) {
		this.setNodeId(nodeId);
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

}
