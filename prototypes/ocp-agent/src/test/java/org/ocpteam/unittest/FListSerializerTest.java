package org.ocpteam.unittest;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.ocpteam.component.FListSerializer;
import org.ocpteam.misc.JLG;
import org.ocpteam.serializable.Address;
import org.ocpteam.serializable.TestObject;

public class FListSerializerTest {
	@Test
	public void mytest() {
		JLG.debug_on();
		JLG.bUseSet = true;
		JLG.set.add(getClass().getName());
		JLG.set.add(FListSerializer.class.getName());
		try {
			// test();
			testMap();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void test() throws Exception {
		FListSerializer ser = new FListSerializer();
		String s = "coucou";
		byte[] array = ser.serialize(s);
		JLG.debug("array=" + JLG.NL + new String(array));
		String s2 = (String) ser.deserialize(array);
		JLG.debug("s2=" + s2);

		int i = 12;
		array = ser.serialize(i);
		JLG.debug("array=" + JLG.NL + new String(array));
		int i2 = (Integer) ser.deserialize(array);
		JLG.debug("i2=" + i2);

		double d = 12.34;
		array = ser.serialize(d);
		JLG.debug("array=" + JLG.NL + new String(array));
		double d2 = (Double) ser.deserialize(array);
		JLG.debug("d2=" + d2);

		byte[] b = "coucou".getBytes();
		array = ser.serialize(b);
		JLG.debug("array=" + JLG.NL + new String(array));
		byte[] b2 = (byte[]) ser.deserialize(array);
		JLG.debug("b2=" + new String(b2));

		TestObject t = new TestObject();
		array = ser.serialize(t);
		JLG.debug("array=" + JLG.NL + new String(array));
		TestObject t2 = (TestObject) ser.deserialize(array);
		JLG.debug("t2=" + t2);
	}

	@SuppressWarnings("unchecked")
	public void testMap() throws Exception {
		FListSerializer ser = new FListSerializer();
		Map<String, Address> map = new HashMap<String, Address>();
		map.put("address1", new Address("0123"));
//		map.put("address2", new Address("1234"));
		JLG.debug("map=" + map);
		byte[] array = ser.serialize((Serializable) map);
		JLG.debug("array=" + JLG.NL + new String(array));
		Map<String, Address> map2 = (Map<String, Address>) ser
				.deserialize(array);
		JLG.debug("map2=" + map2);
	}

}
