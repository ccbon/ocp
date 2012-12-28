package org.ocpteam.interfaces;

import org.ocpteam.serializable.Address;

public interface IAddressMap {

	byte[] get(Address address) throws Exception;

	void put(Address address, byte[] value) throws Exception;

	void remove(Address address) throws Exception;

	IDataStore getLocalMap();

	void setNodeMap(INodeMap nodeMap);

	void setLocalMap(IDataStore datastore);

	void onNodeArrival() throws Exception;

	void onNodeNiceDeparture() throws Exception;

	void networkPicture() throws Exception;

}
