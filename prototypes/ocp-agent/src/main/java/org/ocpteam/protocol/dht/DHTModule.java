package org.ocpteam.protocol.dht;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;
import java.util.Set;

import org.ocpteam.component.Module;
import org.ocpteam.component.Protocol;
import org.ocpteam.entity.Session;
import org.ocpteam.interfaces.IActivity;
import org.ocpteam.interfaces.ITransaction;
import org.ocpteam.misc.JLG;

public class DHTModule extends Module {

	protected static final int STORE = 3001;
	protected static final int RETRIEVE = 3002;
	protected static final int REMOVE = 3003;
	protected static final int KEYSET = 3004;

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

	public IActivity keySet() {
		return new IActivity() {
			
			@Override
			public void run(Session session, Serializable[] objects,
					DataInputStream in, DataOutputStream out, Protocol protocol) throws Exception {
				JLG.debug("keyset...");
				DHTDataSource ds = (DHTDataSource) session.ds();
				Set<String> set = ds.keySet();
				for (String s : set) {
					protocol.getStreamSerializer().writeObject(out, s);
				}
			}

			@Override
			public int getId() {
				return KEYSET;
			}
		};
	}
}
