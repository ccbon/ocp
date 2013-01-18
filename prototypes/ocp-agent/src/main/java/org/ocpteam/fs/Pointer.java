package org.ocpteam.fs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.ocpteam.interfaces.IStructurable;
import org.ocpteam.misc.Structure;
import org.ocpteam.serializable.Address;

/**
 * Pointer is an Id used in a TreeEntry. It specifies the location of the given
 * tree entry. Because a file is potentially big, a pointer can be a array of
 * address.
 * 
 */
public class Pointer implements IStructurable {

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

	@Override
	public Structure toStructure() throws Exception {
		Structure result = new Structure(getClass());
		Address[] addresseArray = addressList.toArray(new Address[0]);
		result.setListField("addressList", addresseArray);
		return result;
	}

	@Override
	public void fromStructure(Structure s) throws Exception {
		Serializable[] list = s.getListField("addressList");
		if (list != null) {
			Address[] addresses = new Address[list.length];
			int i = 0;
			for (Serializable ser : list) {
				addresses[i] = (Address) ser;
				i++;
			}
			addressList = Arrays.asList(addresses);
		}
	}

}
