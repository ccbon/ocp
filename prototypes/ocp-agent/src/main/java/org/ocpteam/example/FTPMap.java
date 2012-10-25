package org.ocpteam.example;

import org.ocpteam.component.FTPPersistentFileMap;
import org.ocpteam.core.TopContainer;
import org.ocpteam.interfaces.IPersistentMap;
import org.ocpteam.misc.JLG;
import org.ocpteam.serializable.Address;

public class FTPMap extends TopContainer {
	public static void main(String[] args) {
		try {
			JLG.debug_on();
			JLG.debug("Start");
			IPersistentMap map = new FTPPersistentFileMap();
			map.setURI("ftp://Yannis:toto@localhost:21/Map");
			
			JLG.debug("Uri set");
			Address a = new Address("1234");
			byte[] b = JLG.serialize(a);
			map.put(a, b);
			byte[] c = map.get(a);
			JLG.debug("b.hashCode = " + b.hashCode());
			JLG.debug("c.hashCode = " + c.hashCode());
			c = map.get(a);
			int i = 0;
			while (i < b.length) {
				if (b[i] != c[i]) {
					JLG.debug("b is different of c");
					break;
				}
				i++;
			}
			if (i == b.length) {
				JLG.debug("b is equal to c");
			}
			Address d = (Address) JLG.deserialize(c);
			JLG.debug("d = " + d);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
