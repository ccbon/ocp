package org.ocpteam.unittest;

import org.junit.Test;
import org.ocpteam.component.FListMarshaler;
import org.ocpteam.interfaces.IMarshaler;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.Structure;

public class FListTest {
	
	@Test
	public void mytest() {
		JLG.debug_on();
		try {
			testFList();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void testFList() throws Exception {
		IMarshaler marshaler = new FListMarshaler();
		org.ocpteam.serializable.Test a = new org.ocpteam.serializable.Test();
		Structure s = a.toStructure();
		JLG.debug("s=" + s);
		byte[] array = marshaler.marshal(s);
		JLG.debug("array=" + new String(array));
		Structure s2 = marshaler.unmarshal(array);
		JLG.debug("s2=" + s2);
//		IStructurable b = s2.toObject();
//		JLG.debug("b=" + b.toStructure().equals(a.toStructure()));
	}

}
