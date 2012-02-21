package org.ocpteam.protocol.ocp;

import org.ocpteam.misc.Id;

public class Pointer extends Id {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Pointer(byte[] val) {
		super(val);
	}

	public Pointer(String string) throws Exception {
		super(string);
	}

	public Pointer(Id id) {
		this(id.getBytes());
	}

	public Key asKey() {
		return new Key(this.getBytes());
	}

}
