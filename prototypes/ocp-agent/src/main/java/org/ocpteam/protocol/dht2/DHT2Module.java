package org.ocpteam.protocol.dht2;

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

public class DHT2Module extends Module {

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
				DHT2DataSource ds = (DHT2DataSource) session.ds();
				DHT2DataModel dm = (DHT2DataModel) ds.getContext().getDataModel();
				String key = (String) objects[0];
				String value = (String) objects[1];
				dm.set(key, value);
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
				DHT2DataSource ds = (DHT2DataSource) session.ds();
				DHT2DataModel dm = (DHT2DataModel) ds.getContext().getDataModel();
				String key = (String) objects[0];
				return dm.get(key);
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
				DHT2DataSource ds = (DHT2DataSource) session.ds();
				String key = (String) objects[0];
				ds.destroy(key);
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
				DHT2DataSource ds = (DHT2DataSource) session.ds();
				Set<String> set = ds.keySet();
				JLG.debug("set=" + set);
				for (String s : set) {
					JLG.debug("write " + s);
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