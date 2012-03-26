package org.ocpteam.protocol.dht;

import java.io.Serializable;
import java.util.Set;

import org.ocpteam.component.Module;
import org.ocpteam.entity.Session;
import org.ocpteam.interfaces.ITransaction;
import org.ocpteam.misc.JLG;

public class DHTModule extends Module {

	protected static final int STORE = 3001;
	protected static final int RETRIEVE = 3002;
	protected static final int REMOVE = 3003;

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

	public ITransaction remove() {
		return new ITransaction() {
			
			@Override
			public Serializable run(Session session, Serializable[] objects)
					throws Exception {
				JLG.debug("remove...");
				DHTDataSource ds = (DHTDataSource) session.ds();
				String key = (String) objects[0];
				ds.remove(key);
				return null;
			}
			
			@Override
			public int getId() {
				return REMOVE;
			}
		};
	}

	public ITransaction keySet() {
		return new ITransaction() {
			
			@Override
			public Serializable run(Session session, Serializable[] objects)
					throws Exception {
				JLG.debug("keyset...");
				DHTDataSource ds = (DHTDataSource) session.ds();
				Set<String> set = ds.keySet();
				String[] array = (String[]) set.toArray(new String[set.size()]);
				return (Serializable) array; 
			}
			
			@Override
			public int getId() {
				// TODO Auto-generated method stub
				return 0;
			}
		};
	}

}
