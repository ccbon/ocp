package org.ocpteam.entity;


public class AddressUser extends User {
	private static final long serialVersionUID = 1L;

	public AddressUser(String username) {
		super(username);
	}

	public String getRootString() {
		return username + "string";
	}
	

}
