package org.ocpteam.entity;

import org.ocpteam.misc.Id;

/**
 * An address is a location on ring. This is generally a message digest (hash).
 * At an address we store a content. The content and its address must be linked
 * by a strong relationships.
 * For instance:
 *  - address=hash(value).
 *  
 *  - or value.signature=hash(address . value.content)
 * 
 */
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
