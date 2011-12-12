package com.guenego.ocp;

import com.guenego.misc.Id;

public class Address extends Id {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Address(byte[] val) {
		super(val);
	}

	public Address(String string) throws Exception {
		super(string);
	}

	public Address(Id id) {
		this(id.getBytes());
	}

}
