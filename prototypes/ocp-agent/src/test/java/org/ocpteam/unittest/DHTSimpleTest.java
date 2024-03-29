package org.ocpteam.unittest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Properties;
import java.util.Set;

import org.ocpteam.component.ContactMap;
import org.ocpteam.component.DSPDataSource;
import org.ocpteam.component.DataSource;
import org.ocpteam.entity.Context;
import org.ocpteam.misc.LOG;
import org.ocpteamx.protocol.dht0.DHTDataModel;
import org.ocpteamx.protocol.dht0.DHTDataSource;

public class DHTSimpleTest {

	@org.junit.Test
	public void mytest() {
		try {
			//assertTrue(new DHTSimpleTest().oneinstance());
			assertTrue(new DHTSimpleTest().twoinstances());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean oneinstance() {
		try {
			LOG.debug_on();
			System.out.println("Hello Test Scenario");
			DataSource ds = new DHTDataSource();
			ds.init();
			Properties p = new Properties();
			p.setProperty("network.coucou", "23");
			p.setProperty("network.hello", "456a");
			p.setProperty("server", "yes");
			p.setProperty("server.port", "12345");
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
			LOG.debug_on();
			DHTDataSource ds = new DHTDataSource();
			ds.init();
			ds.setName("first_agent");
			Properties p = new Properties();
			p.setProperty("network.coucou", "23");
			p.setProperty("server", "yes");
			p.setProperty("server.port", "12347");
			ds.setConfig(p);
			ds.connect();
			DSPDataSource ds2 = new DHTDataSource();
			ds2.init();
			ds2.setName("second_agent");
			Properties p2 = new Properties();
			p2.setProperty("server", "yes");
			p2.setProperty("server.port", "12348");
			p2.setProperty("agent.isFirst", "no");
			p2.setProperty("sponsor.1", "tcp://localhost:12347");
			ds2.setConfig(p2);
			ds2.connect();

			ContactMap cm = ds.getComponent(ContactMap.class);
			LOG.info("contactMap 1 size:" + cm.size());
			LOG.info("contactMap 1 :" + cm);
			
			ContactMap cm2 = ds2.getComponent(ContactMap.class);
			LOG.info("contactMap 2 size:" + cm2.size());
			
			Context ctx = ds.getContext();
			DHTDataModel dm = (DHTDataModel) ctx.getDataModel();
			dm.set("hello", "world");
			ds.remove("hello");
			String value = dm.get("hello");
			LOG.info("hello=" + value);
			assertEquals("world", value);
			dm.remove("hello");
			value = dm.get("hello");
			LOG.info("hello=" + value);
			assertNull(value);
			
			dm.set("hello", "world");
			dm.set("ejder", "bastug");
			dm.set("jean-louis", "guenego");
			Set<String> set = dm.keySet();
			LOG.info("set: " + set);
			assertEquals(3, set.size());
			
			LOG.info("testing sendquick");
			ds.client.sendQuick(ds2.toContact(), "hello".getBytes());
			
			ds.disconnect();
			ds2.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
