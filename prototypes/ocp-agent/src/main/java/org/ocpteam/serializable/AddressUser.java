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
		result.setName(this.getClass());

		result.setSubstructField("address", address);

		return result;
	}

	@Override
	public void fromStructure(Structure s) throws Exception {
		super.fromStructure(s);
		if (s.getStructureFromSubstructField("address") != null) {
			address = (Address) s.getStructureFromSubstructField("address").toStructurable();
		}
	}

}
