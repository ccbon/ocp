package org.ocpteam.fs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.ocpteam.entity.Address;

/**
 * Pointer is an Id used in a TreeEntry. It specifies the location of the given
 * tree entry.
 * Because a file is potentially big, a pointer can be a array of address.
 * 
 */
public class Pointer implements Serializable {

	private static final long serialVersionUID = 1L;
	private List<Address> addressList;

	public Pointer() {
		addressList = new ArrayList<Address>();
	}

	public Pointer(Address address) {
		this();
		add(address);
	}

	public List<Address> getAddresses() {
		return addressList;
	}

	public void add(Address address) {
		addressList.add(address);
	}

}
