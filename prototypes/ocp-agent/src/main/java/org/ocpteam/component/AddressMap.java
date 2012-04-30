package org.ocpteam.component;

import java.util.Map;
import java.util.Queue;

import org.ocpteam.entity.Address;
import org.ocpteam.entity.Contact;
import org.ocpteam.entity.InputMessage;
import org.ocpteam.entity.Response;
import org.ocpteam.interfaces.IAddressMap;

public class AddressMap extends DSContainer<DSPDataSource> implements IAddressMap {

	private NodeMap nodeMap;
	private Map<Address, byte[]> localMap;
	
	public NodeMap getNodeMap() {
		return nodeMap;
	}

	public void setNodeMap(NodeMap nodeMap) {
		this.nodeMap = nodeMap;
	}
	
	public void setLocalMap(Map<Address, byte[]> localMap) {
		this.localMap = localMap;
	}

	@Override
	public byte[] get(Address address) throws Exception {
		if (nodeMap.isResponsible(address)) {
			return localMap.get(address);
		}
		Queue<Contact> contactQueue = nodeMap.getContactQueue(address);
		MapModule m = ds().getComponent(MapModule.class);
		Response r = ds().client.requestByPriority(contactQueue, new InputMessage(m.get(), address));
		return (byte[]) r.getObject();
	}

	@Override
	public void put(Address address, byte[] value) throws Exception {
		if (nodeMap.isResponsible(address)) {
			localMap.put(address, value);
			return;
		}
		Queue<Contact> contactQueue = nodeMap.getContactQueue(address);
		MapModule m = ds().getComponent(MapModule.class);
		ds().client.requestByPriority(contactQueue, new InputMessage(m.put(), address, value));
	}

	@Override
	public void remove(Address address) throws Exception {
		localMap.remove(address);
		if (nodeMap.isResponsible(address)) {
			return;
		}
		Queue<Contact> contactQueue = nodeMap.getContactQueue(address);
		MapModule m = ds().getComponent(MapModule.class);
		ds().client.requestByPriority(contactQueue, new InputMessage(m.remove(), address));
	}

	@Override
	public Map<Address, byte[]> getLocalMap() {
		return localMap;
	}


}
