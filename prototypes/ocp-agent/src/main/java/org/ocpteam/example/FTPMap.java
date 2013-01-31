package org.ocpteam.example;

import org.ocpteam.component.FTPPersistentFileMap;
import org.ocpteam.core.TopContainer;
import org.ocpteam.interfaces.IPersistentMap;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.LOG;
import org.ocpteam.serializable.Address;

public class FTPMap extends TopContainer {
	public static void main(String[] args) {
		try {
			LOG.debug_on();
			LOG.info("Start");
			IPersistentMap map = new FTPPersistentFileMap();
			map.setURI("ftp://Yannis:toto@localhost:21/Map");
			
			LOG.info("Uri set");
			Address a = new Address("1234");
			byte[] b = JLG.serialize(a);
			map.put(a, b);
			byte[] c = map.get(a);
			LOG.info("b.hashCode = " + b.hashCode());
			LOG.info("c.hashCode = " + c.hashCode());
			c = map.get(a);
			int i = 0;
			while (i < b.length) {
				if (b[i] != c[i]) {
					LOG.info("b is different of c");
					break;
				}
				i++;
			}
			if (i == b.length) {
				LOG.info("b is equal to c");
			}
			Address d = (Address) JLG.deserialize(c);
			LOG.info("d = " + d);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
