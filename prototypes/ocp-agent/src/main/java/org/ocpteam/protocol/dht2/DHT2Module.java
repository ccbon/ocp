package org.ocpteam.protocol.dht2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.Serializable;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Set;

import org.ocpteam.component.Protocol;
import org.ocpteam.entity.EOMObject;
import org.ocpteam.entity.Session;
import org.ocpteam.interfaces.IActivity;
import org.ocpteam.interfaces.IModule;
import org.ocpteam.interfaces.ITransaction;
import org.ocpteam.misc.Id;
import org.ocpteam.misc.JLG;

public class DHT2Module implements IModule {

	protected static final int STORE = 3001;
	protected static final int RETRIEVE = 3002;
	protected static final int REMOVE = 3003;
	protected static final int KEYSET = 3004;
	protected static final int TRANSFERSUBMAP = 3005;
	protected static final int SETMAP = 3006;
	protected static final int GETLOCALMAP = 3007;
	protected static final int RESTORE = 3008;

	public ITransaction store() {
		return new ITransaction() {

			@Override
			public Serializable run(Session session, Serializable[] objects)
					throws Exception {
				JLG.debug("storing...");
				DHT2DataSource ds = (DHT2DataSource) session.ds();
				DHT2DataModel dm = (DHT2DataModel) ds.getContext()
						.getDataModel();
				int i = (Integer) objects[0];
				String key = (String) objects[1];
				String value = (String) objects[2];
				dm.set(i, key, value);
				return null;
			}

			@Override
			public int getId() {
				return STORE;
			}

			@Override
			public String getName() {
				return "STORE";
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
				DHT2DataModel dm = (DHT2DataModel) ds.getContext()
						.getDataModel();
				String key = (String) objects[0];
				return dm.get(key);
			}

			@Override
			public int getId() {
				return RETRIEVE;
			}

			@Override
			public String getName() {
				return "RETRIEVE";
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

			@Override
			public String getName() {
				return "REMOVE";
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

	public IActivity transferSubMap() {
		return new IActivity() {

			@Override
			public void run(Session session, Serializable[] objects,
					DataInputStream in, DataOutputStream out, Protocol protocol)
					throws Exception {
				JLG.debug("transfer submap...");
				// normally we should filter the key where hash(key) >= given
				// node_id...
				Id nodeId = (Id) objects[0];
				DHT2DataSource ds = (DHT2DataSource) session.ds();
				Set<String> set = ds.keySet();
				JLG.debug("set=" + set);
				for (String s : set) {
					Id address = ds.getAddress(s);
					if (address.compareTo(nodeId) > 0) {
						JLG.debug("write " + s);
						protocol.getStreamSerializer().writeObject(out, s);
						protocol.getStreamSerializer().writeObject(out,
								ds.retrieve(s));
						ds.destroy(s);
					}
				}
			}

			@Override
			public int getId() {
				return TRANSFERSUBMAP;
			}
		};
	}

	public IActivity setMap() {
		return new IActivity() {

			@Override
			public void run(Session session, Serializable[] objects,
					DataInputStream in, DataOutputStream out, Protocol protocol)
					throws Exception {
				JLG.debug("setMap...");
				// normally we should filter the key where hash(key) >= given
				// node_id...
				DHT2DataSource ds = (DHT2DataSource) session.ds();
				try {
					while (true) {
						Serializable serializable = protocol
								.getStreamSerializer().readObject(in);
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

	public IActivity getLocalMap() {
		return new IActivity() {

			@Override
			public void run(Session session, Serializable[] objects,
					DataInputStream in, DataOutputStream out, Protocol protocol)
					throws Exception {
				JLG.debug("localmap...");
				DHT2DataSource ds = (DHT2DataSource) session.ds();
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
				return GETLOCALMAP;
			}
		};
	}

	public ITransaction restore() {
		return new ITransaction() {
			
			@Override
			public Serializable run(Session session, Serializable[] objects)
					throws Exception {
				JLG.debug("restore...");
				DHT2DataSource ds = (DHT2DataSource) session.ds();
				// find another ring and copy the data from it.
				Id startNodeId = (Id) objects[0];
				Id endNodeId = (Id) objects[1];
				ds.restore(startNodeId, endNodeId);
				return null;
			}
			
			@Override
			public int getId() {
				return RESTORE;
			}

			@Override
			public String getName() {
				return "RESTORE";
			}
		};
	}
}
