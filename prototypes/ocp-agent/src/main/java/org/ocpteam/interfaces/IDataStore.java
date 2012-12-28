package org.ocpteam.interfaces;

import java.util.Set;

import org.ocpteam.serializable.Address;

public interface IDataStore {
	
	public void put(Address a, byte[] b) throws Exception;

	public byte[] get(Address a) throws Exception;

	public void remove(Address address) throws Exception;
	
	public boolean containsKey(Address address) throws Exception;

	public Set<Address> keySet() throws Exception;

	public void putAll(IDataStore datastore) throws Exception;

	public void clear() throws Exception;
}
