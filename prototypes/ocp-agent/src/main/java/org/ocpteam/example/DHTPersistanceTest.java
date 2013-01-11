package org.ocpteam.example;

import java.util.Properties;

import org.ocpteam.component.DSPDataSource;
import org.ocpteam.entity.Context;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.LOG;
import org.ocpteamx.protocol.dht0.DHTDataModel;
import org.ocpteamx.protocol.dht0.DHTDataSource;
import org.ocpteamx.protocol.dht0.DHTModule;

public class DHTPersistanceTest {
	public static void main(String[] args) {
		try {
			LOG.debug_on();
			JLG.bUseSet = true;
			JLG.set.add(DHTPersistanceTest.class.getName());
			JLG.set.add(DHTDataModel.class.getName());
			JLG.set.add(DHTModule.class.getName());
			DHTDataSource ds = new DHTDataSource();
			ds.init();
			ds.setName("first_agent");
			Properties p = new Properties();
			p.setProperty("network.coucou", "23");
			p.setProperty("server", "yes");
			p.setProperty("server.port", "12347");
			ds.setConfig(p);
			ds.connect();
			
			Context ctx = ds.getContext();
			DHTDataModel dm = (DHTDataModel) ctx.getDataModel();
			dm.set("hello2", "world");
			dm.set("coucou", "kiki");
			
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

			Context ctx2 = ds2.getContext();
			DHTDataModel dm2 = (DHTDataModel) ctx2.getDataModel();
			LOG.debug("keyset=" + dm.keySet());
			LOG.debug("keyset=" + dm2.keySet());
			LOG.debug("hello2->" + dm2.get("hello2"));
			LOG.debug("coucou->" + dm2.get("coucou"));
			
			ds.disconnect();
			LOG.debug("hello2->" + dm2.get("hello2"));
			LOG.debug("coucou->" + dm2.get("coucou"));
			ds2.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
