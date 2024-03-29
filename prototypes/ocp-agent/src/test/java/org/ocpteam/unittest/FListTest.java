package org.ocpteam.unittest;

import org.junit.Test;
import org.ocpteam.component.FListMarshaler;
import org.ocpteam.interfaces.IMarshaler;
import org.ocpteam.interfaces.IStructurable;
import org.ocpteam.misc.LOG;
import org.ocpteam.misc.Structure;
import org.ocpteam.serializable.TestObject;

public class FListTest {

	public class EmptyClass implements IStructurable {

		private static final long serialVersionUID = 1L;

		@Override
		public Structure toStructure() throws Exception {
			return new Structure(getClass().getSimpleName());
		}

		@Override
		public void fromStructure(Structure s) throws Exception {
			
		}

	}

	@Test
	public void mytest() {
		LOG.debug_on();
		LOG.bUseSet = true;
		LOG.set.add(FListTest.class.getName());
//		 JLG.set.add(FListMarshaler.class.getName());
		try {
			 testFList();
//			testEmptyClass();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testEmptyClass() throws Exception {
		IMarshaler marshaler = new FListMarshaler();
		EmptyClass a = new EmptyClass();
		Structure s = a.toStructure();
		LOG.info("s=" + s);
		byte[] array = marshaler.marshal(s);
		LOG.info("array=" + new String(array));
		Structure s2 = marshaler.unmarshal(array);
		LOG.info("s2=" + s2);
		LOG.info("b=" + s.equals(s2));
	}

	public void testFList() throws Exception {
		IMarshaler marshaler = new FListMarshaler();
		TestObject a = new TestObject();
		Structure s = a.toStructure();
		LOG.info("s=" + s);
		byte[] array = marshaler.marshal(s);
		LOG.info("array=" + new String(array));
		Structure s2 = marshaler.unmarshal(array);
		LOG.info("s2=" + s2);
		LOG.info("b=" + s.equals(s2));
	}

}
