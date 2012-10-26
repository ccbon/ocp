package org.ocpteam.serializable;

import org.ocpteam.interfaces.IStructurable;
import org.ocpteam.misc.Structure;


public class AddressUser extends User implements IStructurable {
	private static final long serialVersionUID = 1L;
	private Address address;

	public Address getRootAddress() {
		return address;
	}
	public void setRootAddress(Address address) {
		this.address = address;
	}
	
	@Override
	public Structure toStructure() throws Exception {
		Structure result = new Structure("AdresseUser");
		result.setField("address", "substruct", address);
		return null;
	}
	
	@Override
	public void fromStructure(Structure s) throws Exception {
		// TODO Auto-generated method stub
		
	}
	

}
