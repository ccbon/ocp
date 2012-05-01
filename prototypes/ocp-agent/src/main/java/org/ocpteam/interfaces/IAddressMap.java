package org.ocpteam.interfaces;

import java.util.Map;

import org.ocpteam.entity.Address;

public interface IAddressMap {

	byte[] get(Address address) throws Exception;

	void put(Address address, byte[] value) throws Exception;

	void remove(Address address) throws Exception;

	Map<Address, byte[]> getLocalMap();

	void setNodeMap(INodeMap nodeMap);

	void setLocalMap(Map<Address, byte[]> synchronizedMap);

	void onNodeArrival() throws Exception;

	void onNodeNiceDeparture() throws Exception;

	void networkPicture() throws Exception;

}
