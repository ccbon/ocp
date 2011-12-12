package com.guenego.ocp;

import com.guenego.misc.Id;

public class Key extends Id {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Key(byte[] val) {
		super(val);
	}

	public Key(String string) throws Exception {
		super(string);
	}

	public Key(Id id) {
		this(id.getBytes());
	}

}
