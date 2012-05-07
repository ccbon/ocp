package org.ocpteam.entity;


public class AddressUser extends User {
	private static final long serialVersionUID = 1L;

	private Address rootAddress;
	
	public AddressUser(String username) {
		super(username);
	}
	
	public Address getRootAddress() {
		return rootAddress;
	}

	public void setRootAddress(Address rootAddress) {
		this.rootAddress = rootAddress;
	}

}
