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
		Structure result = super.toStructure();
		result.rename(this.getClass());
		result.setSubstructField("address", address.toStructure());
		return result;
	}
	
	@Override
	public void fromStructure(Structure s) throws Exception {
		super.fromStructure(s);
		address = (Address) s.getSubstruct("address").toObject();
	}
	

}
