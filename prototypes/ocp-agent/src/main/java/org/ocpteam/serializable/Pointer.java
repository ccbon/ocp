package org.ocpteam.serializable;

import org.ocpteam.interfaces.IStructurable;
import org.ocpteam.misc.Id;
import org.ocpteam.misc.Structure;

/**
 * Pointer is an Id used in a TreeEntry. It specifies the location of the given
 * tree entry.
 * 
 */
public class Pointer extends Id implements IStructurable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public Pointer() {
	}

	public Pointer(byte[] val) {
		super(val);
	}

	public Pointer(String string) throws Exception {
		super(string);
	}

	public Pointer(Id id) {
		this(id.getBytes());
	}

	@Override
	public Structure toStructure() throws Exception {
		Structure result = new Structure(getClass());
		result.setByteArrayField("value", super.getBytes());
		return result;
	}

	@Override
	public void fromStructure(Structure s) throws Exception {
		super.setValue(s.getBin("value"));
	}

}
