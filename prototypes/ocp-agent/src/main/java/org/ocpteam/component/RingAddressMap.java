package org.ocpteam.component;

import java.util.Map;
import java.util.Queue;

import org.ocpteam.entity.Address;
import org.ocpteam.entity.Contact;
import org.ocpteam.entity.InputMessage;
import org.ocpteam.entity.Response;
import org.ocpteam.interfaces.IAddressMap;
import org.ocpteam.interfaces.INodeMap;

public class RingAddressMap extends DSContainer<DSPDataSource> implements
		IAddressMap {

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
		for (int i = 0; i < ringNodeMap.getRingNbr(); i++) {
			value = get(i, address);
			if (value != null) {
				break;
			}
		}
		return value;
	}

	public byte[] get(int ring, Address address) throws Exception {
		NodeMap nodeMap = ringNodeMap.getNodeMaps()[ring];
		if (nodeMap.isEmpty()) {
			return null;
		}
		if (nodeMap.isResponsible(address)) {
			return localMap.get(address);
		}
		Queue<Contact> contactQueue = nodeMap.getContactQueue(address);
		RingMapModule m = ds().getComponent(RingMapModule.class);
		Response r = ds().client.requestByPriority(contactQueue,
				new InputMessage(m.getOnRing(), ring, address));
		return (byte[]) r.getObject();
	}

	@Override
	public void put(Address address, byte[] value) throws Exception {
		// Strategy: foreach ring put the <address, value>
		for (int i = 0; i < ringNodeMap.getRingNbr(); i++) {
			put(i, address, value);
		}
	}

	public void put(int ring, Address address, byte[] value) throws Exception {
		NodeMap nodeMap = ringNodeMap.getNodeMaps()[ring];
		if (nodeMap.isEmpty()) {
			return;
		}
		if (nodeMap.isResponsible(address)) {
			localMap.put(address, value);
			return;
		}
		Queue<Contact> contactQueue = nodeMap.getContactQueue(address);
		RingMapModule m = ds().getComponent(RingMapModule.class);
		ds().client.requestByPriority(contactQueue, new InputMessage(m.putOnRing(),
				ring, address, value));
	}

	@Override
	public void remove(Address address) throws Exception {
		// Strategy: foreach ring remove the <address>
		for (int i = 0; i < ringNodeMap.getRingNbr(); i++) {
			remove(i, address);
		}
	}

	public void remove(int ring, Address address) throws Exception {
		// Strategy: foreach nodeMap remove the address
		NodeMap nodeMap = ringNodeMap.getNodeMaps()[ring];
		if (nodeMap.isEmpty()) {
			return;
		}

		if (nodeMap.isResponsible(address)) {
			localMap.remove(address);
			return;
		}
		Queue<Contact> contactQueue = nodeMap.getContactQueue(address);
		RingMapModule m = ds().getComponent(RingMapModule.class);
		ds().client.requestByPriority(contactQueue, new InputMessage(
				m.removeOnRing(), ring, address));

	}

	@Override
	public Map<Address, byte[]> getLocalMap() {
		return localMap;
	}

	@Override
	public void setLocalMap(Map<Address, byte[]> localMap) {
		this.localMap = localMap;

	}

	@Override
	public void setNodeMap(INodeMap nodeMap) {
		this.ringNodeMap = (RingNodeMap) nodeMap;
	}

}
