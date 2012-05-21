package org.ocpteam.component;

import java.io.EOFException;
import java.io.Serializable;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.ocpteam.entity.Session;
import org.ocpteam.interfaces.IActivity;
import org.ocpteam.interfaces.IAddressMap;
import org.ocpteam.interfaces.IModule;
import org.ocpteam.interfaces.ITransaction;
import org.ocpteam.misc.Id;
import org.ocpteam.misc.JLG;
import org.ocpteam.serializable.Address;
import org.ocpteam.serializable.EOMObject;

public class MapModule implements IModule {

	protected static final int GET = 4001;
	protected static final int PUT = 4002;
	protected static final int REMOVE = 4003;
	protected static final int GETLOCALMAP = 4004;
	protected static final int SETMAP = 4005;
	protected static final int TRANSFERSUBMAP = 4006;

	public ITransaction get() {
		return new ITransaction() {
			
			@Override
			public Serializable run(Session session, Serializable[] objects)
					throws Exception {
				JLG.debug("GET...");
				Address address = (Address) objects[0];
				return session.ds().getComponent(IAddressMap.class).get(address);
			}
			
			@Override
			public String getName() {
				return "GET";
			}
			
			@Override
			public int getId() {
				return GET;
			}
		};
	}

	public ITransaction put() {
		return new ITransaction() {
			
			@Override
			public Serializable run(Session session, Serializable[] objects)
					throws Exception {
				JLG.debug("PUT...");
				Address address = (Address) objects[0];
				byte[] value = (byte[]) objects[1];
				session.ds().getComponent(IAddressMap.class).put(address, value);
				return null;
			}
			
			@Override
			public String getName() {
				return "PUT";
			}
			
			@Override
			public int getId() {
				return PUT;
			}
		};
	}

	public ITransaction remove() {
		return new ITransaction() {
			
			@Override
			public Serializable run(Session session, Serializable[] objects)
					throws Exception {
				JLG.debug("REMOVE...");
				Address address = (Address) objects[0];
				session.ds().getComponent(IAddressMap.class).remove(address);
				return null;
			}
			
			@Override
			public String getName() {
				return "REMOVE";
			}
			
			@Override
			public int getId() {
				return REMOVE;
			}
		};
	}
	
	public IActivity getLocalMap() {
		return new IActivity() {

			@Override
			public void run(Session session, Serializable[] objects, Socket socket, Protocol protocol)
					throws Exception {
				try {
				JLG.debug("localmap...");
				Map<Address, byte[]> map = session.ds().getComponent(IAddressMap.class).getLocalMap();
				Set<Address> set = map.keySet();
				for (Address address : set) {
					JLG.debug("write " + address);
					protocol.getStreamSerializer().writeObject(socket, address);
					JLG.debug("sha1(value)=" + JLG.sha1(map.get(address)));
					protocol.getStreamSerializer().writeObject(socket,
							map.get(address));
					// wait for ACK (null object)
					int i = (Integer) protocol.getStreamSerializer().readObject(socket);
					JLG.debug("ack received: " + i);		
				}
				} catch (Exception e) {
					e.printStackTrace();
					throw e;
				}
			}

			@Override
			public int getId() {
				return GETLOCALMAP;
			}

		};
	}
	
	public IActivity setMap() {
		return new IActivity() {

			@Override
			public void run(Session session, Serializable[] objects,
					Socket socket, Protocol protocol)
					throws Exception {
				JLG.debug("setMap...");
				try {
					while (true) {
						Serializable serializable = protocol
								.getStreamSerializer().readObject(socket);
						if (serializable instanceof EOMObject) {
							break;
						}
						Address address = (Address) serializable;
						byte[] value = (byte[]) protocol.getStreamSerializer()
								.readObject(socket);
						session.ds().getComponent(IAddressMap.class).getLocalMap().put(address, value);
						protocol.getStreamSerializer().writeObject(socket, null);
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
	
	public IActivity transferSubMap() {
		return new IActivity() {

			@Override
			public void run(Session session, Serializable[] objects,
					Socket socket, Protocol protocol)
					throws Exception {
				JLG.debug("transfer submap...");
				Id nodeId = (Id) objects[0];
				Map<Address, byte[]> localMap = session.ds().getComponent(IAddressMap.class).getLocalMap();
				Set<Address> set = new HashSet<Address>(localMap.keySet());
				for (Address address : set) {
					if (address.compareTo(nodeId) > 0) {
						JLG.debug("write " + address);
						protocol.getStreamSerializer().writeObject(socket, address);
						protocol.getStreamSerializer().writeObject(socket,
								localMap.get(address));
						localMap.remove(address);
					}
				}
			}

			@Override
			public int getId() {
				return TRANSFERSUBMAP;
			}
		};
	}

}
