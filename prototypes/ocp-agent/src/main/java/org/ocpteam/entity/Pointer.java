package org.ocpteam.entity;

import org.ocpteam.misc.Id;

/**
 * Pointer is an Id used in a TreeEntry. It specifies the location of the given
 * tree entry.
 * 
 */
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

}
