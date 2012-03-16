package org.ocpteam.protocol.ocp;

import java.util.Map;
import java.util.TreeMap;

import org.ocpteam.component.Agent;
import org.ocpteam.misc.Id;


public class Node implements Comparable<Node> {

	public Id id;
	public Agent agent;
	private Map<Id, byte[]> contentMap;

	public Node(Id id, Agent agent) {
		this.id = id;
		this.agent = agent;
		this.contentMap = new TreeMap<Id, byte[]>();
	}

	@Override
	public int compareTo(Node o) {
		return id.compareTo(o.id);
	}

	public void store(Id address, byte[] content) throws Exception {
		contentMap.put(address, content);
	}

}
