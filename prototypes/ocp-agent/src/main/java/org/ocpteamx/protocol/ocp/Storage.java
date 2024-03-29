package org.ocpteamx.protocol.ocp;

import java.util.Iterator;
import java.util.NavigableSet;
import java.util.TreeSet;

import org.ocpteam.interfaces.IDataStore;
import org.ocpteam.interfaces.IPersistentMap;
import org.ocpteam.misc.Application;
import org.ocpteam.misc.Id;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.LOG;
import org.ocpteam.serializable.Address;

public class Storage {

	public NavigableSet<Id> nodeSet; // set of node referenced by their id
	private IDataStore datastore;
	public OCPAgent agent;
	private OCPDataSource ds;

	public Storage(OCPDataSource ds) throws Exception {
		this.ds = ds;
		this.agent = ds.getComponent(OCPAgent.class);
		nodeSet = new TreeSet<Id>();
		String root = agent.ds()
				.getProperty(
						"storage.dir",
						Application.getAppDir() + "/datastore/ocp/"
								+ agent.getName());
		IPersistentMap persistentMap = (IPersistentMap) agent.ds()
				.getComponent(IDataStore.class);
		persistentMap.setURI(root);
		datastore = persistentMap;
	}

	public void attach() throws Exception {
		// at least we must have one node
		if (nodeSet.size() == 0) {
			Id[] nodeIds = null;
			if (ds.isFirstAgent()) {
				nodeIds = new Id[1];
				nodeIds[0] = agent.generateId();
			} else {
				nodeIds = agent.getClient().requestNodeId();
			}
			if (nodeIds != null) {
				for (int i = 0; i < nodeIds.length; i++) {
					nodeSet.add(nodeIds[i]);
				}
			}

		}
	}

	public void put(Address address, Content content) throws Exception {
		datastore.put(address, agent.ds().serializer.serialize(content));
		// Rude detachment: now tell to your agent backuper what you have
		// stored.
		// declare(ADD, address, data.getKey(agent));
	}

	public Content get(Address address) {
		try {
			byte[] array = datastore.get(address);
			if (array == null) {
				return null;
			} else {
				return (Content) agent.ds().serializer.deserialize(array);
			}
		} catch (Exception e) {
			LOG.error(e);
			return null;
		}
	}

	@Override
	public String toString() {
		try {
			String result = "nodeMap=" + JLG.NL;
			Iterator<Id> it = nodeSet.iterator();
			while (it.hasNext()) {
				Id id = it.next();
				result += id + JLG.NL;
			}
			result += "Content=" + JLG.NL;
			Iterator<Address> itc = datastore.keySet().iterator();
			while (itc.hasNext()) {
				Address address = itc.next();
				Content content = null;
				try {
					content = (Content) agent.ds().serializer
							.deserialize(datastore.get(address));
				} catch (Exception e) {
					LOG.error(e);
				}
				result += address + "->" + content + JLG.NL;
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return super.toString();
		}
	}

	public boolean contains(Address address) throws Exception {
		return datastore.containsKey(address);
	}

	public void remove(Address address, byte[] addressSignature)
			throws Exception {
		// TODO Auto-generated method stub
		// retrieve the public key of the user and check the address signature
		Content content = get(address);
		if (content == null) {
			return;
		}
		UserPublicInfo upi = agent.getUserPublicInfo(content.username);
		if (upi.verify(agent, address.getBytes(), addressSignature) == false) {
			throw new Exception(
					"Cannot remove data. Verifying signature failed.");
		}
		// JLG.debug("signature ok");
		datastore.remove(address);
	}

	public void removeAll() throws Exception {
		datastore.clear();
	}

}
