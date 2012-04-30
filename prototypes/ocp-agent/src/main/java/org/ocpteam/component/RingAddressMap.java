package org.ocpteam.component;

import java.util.Map;
import java.util.Queue;

import org.ocpteam.entity.Address;
import org.ocpteam.entity.Contact;
import org.ocpteam.entity.InputMessage;
import org.ocpteam.entity.Response;
import org.ocpteam.interfaces.IAddressMap;
import org.ocpteam.interfaces.INodeMap;

public class RingAddressMap extends DSContainer<DSPDataSource> implements IAddressMap {

	private RingNodeMap ringNodeMap;
	private Map<Address, byte[]> localMap;

	@Override
	public byte[] get(Address address) throws Exception {
		byte[] value = null;
		if (ringNodeMap.isResponsible(address)) {
			value = localMap.get(address);
			if (value != null) {
				return value;
			}
		}
		// not found locally so look at every ring.
		for (NodeMap nodeMap : ringNodeMap.getNodeMaps()) {
			Queue<Contact> contactQueue = nodeMap.getContactQueue(address);
			MapModule m = ds().getComponent(MapModule.class);
			Response r = ds().client.requestByPriority(contactQueue, new InputMessage(m.get(), address));
			value = (byte[]) r.getObject();
			if (value != null) {
				break;
			}
		}
		return value;
	}

	@Override
	public void put(Address address, byte[] value) throws Exception {
		// Strategy: foreach nodeMap put the <address, value>
		for (NodeMap nodeMap : ringNodeMap.getNodeMaps()) {
			if (nodeMap.isResponsible(address)) {
				localMap.put(address, value);
				continue;
			}
			Queue<Contact> contactQueue = nodeMap.getContactQueue(address);
			MapModule m = ds().getComponent(MapModule.class);
			ds().client.requestByPriority(contactQueue, new InputMessage(m.put(), address, value));

		}		
	}

	@Override
	public void remove(Address address) throws Exception {
		// Strategy: foreach nodeMap remove the address
		for (NodeMap nodeMap : ringNodeMap.getNodeMaps()) {
			if (nodeMap.isResponsible(address)) {
				localMap.remove(address);
				continue;
			}
			Queue<Contact> contactQueue = nodeMap.getContactQueue(address);
			MapModule m = ds().getComponent(MapModule.class);
			ds().client.requestByPriority(contactQueue, new InputMessage(m.remove(), address));

		}		
	}

	@Override
	public Map<Address, byte[]> getLocalMap() {
		return localMap;
	}

	public void setLocalMap(Map<Address, byte[]> localMap) {
		this.localMap = localMap;
		
	}

	@Override
	public void setNodeMap(INodeMap nodeMap) {
		this.ringNodeMap = (RingNodeMap) nodeMap;
	}

}
