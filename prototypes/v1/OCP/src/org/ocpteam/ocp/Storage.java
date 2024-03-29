package org.ocpteam.ocp;

import java.util.Iterator;
import java.util.Map;
import java.util.NavigableSet;
import java.util.TreeSet;

import org.ocpteam.misc.Id;
import org.ocpteam.misc.JLG;


public class Storage {

	public NavigableSet<Id> nodeSet; // set of node referenced by their id
	private Map<Address, Content> contentMap;
	public OCPAgent agent;

	public Storage(OCPAgent agent) {
		nodeSet = new TreeSet<Id>();
		contentMap = new PersistentHashMap(agent);
		this.agent = agent;
	}

	public void attach() throws Exception {
		// at least we must have one node
		if (nodeSet.size() == 0) {
			Id[] nodeIds = null;
			if (agent.isFirstAgent()) {
				nodeIds = new Id[1];
				nodeIds[0] = agent.generateId();
			} else {
				nodeIds = agent.client.requestNodeId();
			}
			if (nodeIds != null) {
				for (int i = 0; i < nodeIds.length; i++) {
					nodeSet.add(nodeIds[i]);
				}
			}

		}
		// declare to all my contact that I am attached.
		agent.declareContact();

	}

	public void put(Address address, Content data) throws Exception {
		contentMap.put(address, data);
		// Rude detachment: now tell to your agent backuper what you have stored.
		// declare(ADD, address, data.getKey(agent));
	}

	public Content get(Address address) {
		return contentMap.get(address);
	}

	@Override
	public String toString() {
		String result = "nodeMap=" + JLG.NL;
		Iterator<Id> it = nodeSet.iterator();
		while (it.hasNext()) {
			Id id = (Id) it.next();
			result += id + JLG.NL;
		}
		result += "Content=" + JLG.NL;
		Iterator<Address> itc = contentMap.keySet().iterator();
		while (itc.hasNext()) {
			Address id = (Address) itc.next();
			result += id + "->" + contentMap.get(id) + JLG.NL;
		}
		
		return result;
	}

	public boolean contains(Address address) {
		return contentMap.containsKey(address);
	}

	public void remove(Address address, byte[] addressSignature) throws Exception {
		// TODO Auto-generated method stub
		// retrieve the public key of the user and check the address signature
		Content content = contentMap.get(address);
		if (content == null) {
			return;
		}
		UserPublicInfo upi = agent.getUserPublicInfo(content.username);
		if (upi.verify(agent, address.getBytes(), addressSignature) == false) {
			throw new Exception("Cannot remove data. Verifying signature failed.");
		}
		//JLG.debug("signature ok");
		contentMap.remove(address);
	}

	public void removeAll() {
		contentMap.clear();
	}

}
