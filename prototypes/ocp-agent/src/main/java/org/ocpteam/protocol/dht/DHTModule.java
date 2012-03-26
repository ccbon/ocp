package org.ocpteam.protocol.dht;

import java.io.Serializable;

import org.ocpteam.component.Module;
import org.ocpteam.entity.Session;
import org.ocpteam.interfaces.ITransaction;
import org.ocpteam.misc.JLG;

public class DHTModule extends Module {

	protected static final int STORE = 3001;
	protected static final int RETRIEVE = 3002;

	public ITransaction store() {
		return new ITransaction() {

			@Override
			public Serializable run(Session session, Serializable[] objects)
					throws Exception {
				JLG.debug("storing...");
				DHTDataSource ds = (DHTDataSource) session.ds();
				String key = (String) objects[0];
				String value = (String) objects[1];
				ds.store(key, value);
				return null;
			}

			@Override
			public int getId() {
				return STORE;
			}
		};
	}

	public ITransaction retrieve() {
		return new ITransaction() {
			
			@Override
			public Serializable run(Session session, Serializable[] objects)
					throws Exception {
				JLG.debug("retrieving...");
				DHTDataSource ds = (DHTDataSource) session.ds();
				String key = (String) objects[0];
				return ds.retrieve(key);
			}
			
			@Override
			public int getId() {
				return RETRIEVE;
			}
		};
	}

}
