package org.ocpteam.serializable;

import java.io.Serializable;

import org.ocpteam.interfaces.IStructurable;
import org.ocpteam.misc.Structure;


/**
 * A Contact reflects the public information that an agent can gives to the
 * others members of the distributed network.
 *
 */
public class Contact implements Serializable, IStructurable {

	private static final long serialVersionUID = 1L;
	
	private String name;
	private String host;
	private int tcpPort;
	private int udpPort;
	private Node node;

	private boolean bIsMyself = false;
	
	public Contact() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;	
	}
	
	public boolean isMyself() {
		return bIsMyself;
	}
	
	public void setMyself(boolean b) {
		this.bIsMyself = b;
	}
	
	public void setHost(String host) {
		this.host = host;
	}
	
	public String getHost() throws Exception {
		if (host == null) {
			throw new Exception("host is null !");
		}
		return host;
	}
	
	@Override
	public String toString() {
		String result = getName();
		if (isMyself()) {
			result += "<myself>";
		}
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj.getClass().equals(this.getClass())) {
			return ((Contact) obj).getName().equals(getName());
		}
		return false;
	}

	public int getTcpPort() {
		return tcpPort;
	}

	public void setTcpPort(int tcpPort) {
		this.tcpPort = tcpPort;
	}

	public int getUdpPort() {
		return udpPort;
	}

	public void setUdpPort(int udpPort) {
		this.udpPort = udpPort;
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

	@Override
	public Structure toStructure() throws Exception {
		Structure result = new Structure(Contact.class);
		result.setStringField("name", getName());
		result.setStringField("host",  getHost());
		result.setIntField("tcpPort", getTcpPort());
		result.setIntField("udpPort", getUdpPort());
		result.setSubstructField("node", getNode().toStructure());
		return result;
	}

	@Override
	public void fromStructure(Structure s) throws Exception {
		setName(s.getString("name"));
		setHost(s.getString("host"));
		setTcpPort(s.getInt("tcpPort"));
		setUdpPort(s.getInt("udpPort"));
		setNode((Node) s.getSubstruct("node").toObject());
	}


}
