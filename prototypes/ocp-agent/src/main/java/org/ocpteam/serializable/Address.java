package org.ocpteam.serializable;

import org.ocpteam.interfaces.IStructurable;
import org.ocpteam.misc.Id;
import org.ocpteam.misc.Structure;

/**
 * An address is a location on ring. This is generally a message digest (hash).
 * At an address we store a content. The content and its address must be linked
 * by a strong relationships. For instance: - address=hash(value).
 * 
 * - or value.signature=hash(address . value.content)
 * 
 */
public class Address extends Id implements IStructurable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public Address() {
		
	}

	public Address(byte[] val) {
		super(val);
	}

	public Address(String string) throws Exception {
		super(string);
	}

	public Address(Id id) {
		this(id.getBytes());
	} 

	@Override
	public Structure toStructure() throws Exception {
		Structure result = new Structure(Address.class);
		result.setBinField("id", getBytes());
		return result;
	}

	@Override
	public void fromStructure(Structure s) throws Exception {
		setValue((byte[]) s.getFieldValue("id"));
	}

}
