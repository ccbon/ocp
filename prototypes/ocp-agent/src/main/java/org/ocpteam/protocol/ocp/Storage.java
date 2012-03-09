package org.ocpteam.protocol.ocp;

import java.util.Iterator;
import java.util.Map;
import java.util.NavigableSet;
import java.util.TreeSet;

import org.ocpteam.misc.Id;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.PersistentHashMap;


public class Storage {

	public NavigableSet<Id> nodeSet; // set of node referenced by their id
	private Map<byte[], byte[]> contentMap;
	public OCPAgent agent;

	public Storage(OCPAgent agent) {
		nodeSet = new TreeSet<Id>();
		String root = agent.cfg.getProperty(
				"storage.dir",
				System.getenv("TEMP") + "/ocp_agent_storage/"
						+ agent.getName());
		contentMap = new PersistentHashMap(root);
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

	public void put(Address address, Content content) throws Exception {
		contentMap.put(address.getBytes(), JLG.serialize(content));
		// Rude detachment: now tell to your agent backuper what you have stored.
		// declare(ADD, address, data.getKey(agent));
	}

	public Content get(Address address) {
		try {
			byte[] array = contentMap.get(address.getBytes());
			if (array == null) {
				return null;
			} else {
				return (Content) JLG.deserialize(array);
			}
		} catch (Exception e) {
			JLG.error(e);
			return null;
		}
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
		Iterator<byte[]> itc = contentMap.keySet().iterator();
		while (itc.hasNext()) {
			byte[] id = itc.next();
			Address address = new Address(id);
			Content content = null;
			try {
				content = (Content) JLG.deserialize(contentMap.get(id));
			} catch (Exception e) {
				JLG.error(e);
			}
			result += address + "->" +  content + JLG.NL;
		}		
		return result;
	}

	public boolean contains(Address address) {
		return contentMap.containsKey(address.getBytes());
	}

	public void remove(Address address, byte[] addressSignature) throws Exception {
		// TODO Auto-generated method stub
		// retrieve the public key of the user and check the address signature
		Content content = get(address);
		if (content == null) {
			return;
		}
		UserPublicInfo upi = agent.getUserPublicInfo(content.username);
		if (upi.verify(agent, address.getBytes(), addressSignature) == false) {
			throw new Exception("Cannot remove data. Verifying signature failed.");
		}
		//JLG.debug("signature ok");
		contentMap.remove(address.getBytes());
	}

	public void removeAll() {
		contentMap.clear();
	}

}
