package org.ocpteam.ocp_agent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Properties;
import java.util.Set;

import org.ocpteam.component.ContactMap;
import org.ocpteam.component.DSPDataSource;
import org.ocpteam.component.DataSource;
import org.ocpteam.entity.Context;
import org.ocpteam.interfaces.IMapDataModel;
import org.ocpteam.misc.JLG;
import org.ocpteam.protocol.dht.DHTDataSource;

public class DHTSimpleTest {

	@org.junit.Test
	public void mytest() {
		try {
			assertTrue(new DHTSimpleTest().oneinstance());
			//assertTrue(new DHTSimpleTest().twoinstances());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean oneinstance() {
		try {
			JLG.debug_on();
			System.out.println("Hello Test Scenario");
			DataSource ds = new DHTDataSource();
			ds.init();
			Properties p = new Properties();
			p.setProperty("network.coucou", "23");
			p.setProperty("network.hello", "456a");
			p.setProperty("server", "yes");
			p.setProperty("listener.tcp.url", "tcp://localhost:12345");
			ds.setConfig(p);
			ds.connect();
			ds.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean twoinstances() {
		try {
			JLG.debug_on();
			DHTDataSource ds = new DHTDataSource();
			ds.init();
			ds.setName("first_agent");
			Properties p = new Properties();
			p.setProperty("network.coucou", "23");
			p.setProperty("server", "yes");
			p.setProperty("listener.tcp.url", "tcp://localhost:12347");
			ds.setConfig(p);
			ds.connect();
			DSPDataSource ds2 = new DHTDataSource();
			ds2.init();
			ds2.setName("second_agent");
			Properties p2 = new Properties();
			p2.setProperty("server", "yes");
			p2.setProperty("listener.tcp.url", "tcp://localhost:12348");
			p2.setProperty("agent.isFirst", "no");
			p2.setProperty("sponsor.1", "tcp://localhost:12347");
			ds2.setConfig(p2);
			ds2.connect();

			ContactMap cm = ds.getComponent(ContactMap.class);
			JLG.debug("contactMap 1 size:" + cm.size());
			JLG.debug("contactMap 1 :" + cm);
			
			ContactMap cm2 = ds2.getComponent(ContactMap.class);
			JLG.debug("contactMap 2 size:" + cm2.size());
			
			Context ctx = ds.getContext();
			IMapDataModel dm = (IMapDataModel) ctx.getDataModel();
			dm.set("hello", "world");
			ds.remove("hello");
			String value = dm.get("hello");
			JLG.debug("hello=" + value);
			assertEquals("world", value);
			dm.remove("hello");
			value = dm.get("hello");
			JLG.debug("hello=" + value);
			assertNull(value);
			
			dm.set("hello", "world");
			dm.set("ejder", "bastug");
			dm.set("jean-louis", "guenego");
			Set<String> set = dm.keySet();
			JLG.debug("set: " + set);
			assertEquals(3, set.size());
			
			ds.disconnect();
			ds2.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
