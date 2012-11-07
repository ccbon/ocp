package org.ocpteam.serializable;

import org.ocpteam.interfaces.IStructurable;
import org.ocpteam.misc.Structure;

public class Test implements IStructurable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int a;
	String e;
	byte[] b;

	@Override
	public Structure toStructure() throws Exception {
		Structure result = new Structure(getClass());
		result.setIntField("a", 1);
		result.setBytesField("b", new byte[] {23,45});
		result.setStringField("e", "hello\\world");
		return result;
	}

	@Override
	public void fromStructure(Structure s) throws Exception {
		e = s.getString("e");
		a = s.getInt("a");
		b = s.getByteArray("b");
	}

}
