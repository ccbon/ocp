package org.ocpteam.serializable;

import java.io.Serializable;

import org.ocpteam.interfaces.IStructurable;
import org.ocpteam.misc.Id;
import org.ocpteam.misc.Structure;

public class Node implements Serializable, IStructurable {

	private Id nodeId;
	private int ring;

	public Node(Id nodeId) {
		this.setNodeId(nodeId);
	}

	public Node(Id nodeId, int ring) {
		this(nodeId);
		this.setRing(ring);
	}

	public Node() {

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
		return "node:" + ring + "-" + nodeId.toString();
	}

	public int getRing() {
		return ring;
	}

	public void setRing(int ring) {
		this.ring = ring;
	}

	@Override
	public Structure toStructure() throws Exception {
		Structure result = new Structure(getClass());
		result.setByteArrayField("nodeId", nodeId.getBytes());
		result.setIntField("ring", getRing());
		return result;
	}

	@Override
	public void fromStructure(Structure s) throws Exception {
		setRing(s.getInt("ring"));
		setNodeId(new Id(s.getBin("nodeId")));
	}

}
