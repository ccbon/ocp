package org.ocpteam.protocol.dht1;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.Serializable;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Set;

import org.ocpteam.component.Module;
import org.ocpteam.component.Protocol;
import org.ocpteam.entity.EOMObject;
import org.ocpteam.entity.Session;
import org.ocpteam.interfaces.IActivity;
import org.ocpteam.interfaces.ITransaction;
import org.ocpteam.misc.JLG;

public class DHT1Module extends Module {

	protected static final int STORE = 3001;
	protected static final int RETRIEVE = 3002;
	protected static final int REMOVE = 3003;
	protected static final int KEYSET = 3004;
	protected static final int SUBMAP = 3005;
	protected static final int SETMAP = 3006;

	public ITransaction store() {
		return new ITransaction() {

			@Override
			public Serializable run(Session session, Serializable[] objects)
					throws Exception {
				JLG.debug("storing...");
				DHT1DataSource ds = (DHT1DataSource) session.ds();
				DHT1DataModel dm = (DHT1DataModel) ds.getContext()
						.getDataModel();
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
				DHT1DataSource ds = (DHT1DataSource) session.ds();
				DHT1DataModel dm = (DHT1DataModel) ds.getContext()
						.getDataModel();
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
				DHT1DataSource ds = (DHT1DataSource) session.ds();
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
					DataInputStream in, DataOutputStream out, Protocol protocol)
					throws Exception {
				JLG.debug("keyset...");
				DHT1DataSource ds = (DHT1DataSource) session.ds();
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

	public IActivity subMap() {
		return new IActivity() {

			@Override
			public void run(Session session, Serializable[] objects,
					DataInputStream in, DataOutputStream out, Protocol protocol)
					throws Exception {
				JLG.debug("submap...");
				// normally we should filter the key where hash(key) >= given
				// node_id...
				DHT1DataSource ds = (DHT1DataSource) session.ds();
				Set<String> set = ds.keySet();
				JLG.debug("set=" + set);
				for (String s : set) {
					JLG.debug("write " + s);
					protocol.getStreamSerializer().writeObject(out, s);
					protocol.getStreamSerializer().writeObject(out,
							ds.retrieve(s));
					
				}
			}

			@Override
			public int getId() {
				return SUBMAP;
			}
		};
	}

	public IActivity setMap() {
		// TODO Auto-generated method stub
		return new IActivity() {

			@Override
			public void run(Session session, Serializable[] objects,
					DataInputStream in, DataOutputStream out, Protocol protocol)
					throws Exception {
				JLG.debug("setMap...");
				// normally we should filter the key where hash(key) >= given
				// node_id...
				DHT1DataSource ds = (DHT1DataSource) session.ds();
				try {
					while (true) {
						Serializable serializable = protocol.getStreamSerializer()
								.readObject(in);
						if (serializable instanceof EOMObject) {
							break;
						}
						String key = (String) serializable;
						String value = (String) protocol.getStreamSerializer()
								.readObject(in);
						ds.store(key, value);
						protocol.getStreamSerializer().writeObject(out, null);
					}
					
				} catch (SocketException e) {
				} catch (EOFException e) {
				} catch (SocketTimeoutException e) {
					
				}

			}

			@Override
			public int getId() {
				return SETMAP;
			}
		};
	}
}
