package org.ocpteam.serializable;


public class AddressUser extends User {
	private static final long serialVersionUID = 1L;
	private Address address;

	public Address getRootAddress() {
		return address;
	}
	public void setRootAddress(Address address) {
		this.address = address;
	}
	

}
