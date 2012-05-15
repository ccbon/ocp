package org.ocpteam.component;

import java.io.Serializable;

import org.ocpteam.entity.Session;
import org.ocpteam.interfaces.IAddressMap;
import org.ocpteam.interfaces.IModule;
import org.ocpteam.interfaces.ITransaction;
import org.ocpteam.misc.JLG;
import org.ocpteam.serializable.Address;

public class RingMapModule implements IModule {

	protected static final int GET_ON_RING = 5001;
	protected static final int PUT_ON_RING = 5002;
	protected static final int REMOVE_ON_RING = 5003;

	public ITransaction getOnRing() {
		return new ITransaction() {
			
			@Override
			public Serializable run(Session session, Serializable[] objects)
					throws Exception {
				JLG.debug("GET_ON_RING...");
				int ring = (Integer) objects[0];
				Address address = (Address) objects[1];
				RingAddressMap map = (RingAddressMap) session.ds().getComponent(IAddressMap.class);
				return map.get(ring, address);
			}
			
			@Override
			public String getName() {
				return "GET_ON_RING";
			}
			
			@Override
			public int getId() {
				return GET_ON_RING;
			}
		};
	}

	public ITransaction putOnRing() {
		return new ITransaction() {
			
			@Override
			public Serializable run(Session session, Serializable[] objects)
					throws Exception {
				JLG.debug("PUT_ON_RING...");
				int ring = (Integer) objects[0];
				Address address = (Address) objects[1];
				byte[] value = (byte[]) objects[2];
				RingAddressMap map = (RingAddressMap) session.ds().getComponent(IAddressMap.class);
				map.put(ring, address, value);
				return null;
			}
			
			@Override
			public String getName() {
				return "PUT_ON_RING";
			}
			
			@Override
			public int getId() {
				return PUT_ON_RING;
			}
		};
	}

	public ITransaction removeOnRing() {
		return new ITransaction() {
			
			@Override
			public Serializable run(Session session, Serializable[] objects)
					throws Exception {
				JLG.debug("REMOVE_ON_RING...");
				int ring = (Integer) objects[0];
				Address address = (Address) objects[1];
				RingAddressMap map = (RingAddressMap) session.ds().getComponent(IAddressMap.class);
				map.remove(ring, address);
				return null;
			}
			
			@Override
			public String getName() {
				return "REMOVE_ON_RING";
			}
			
			@Override
			public int getId() {
				return REMOVE_ON_RING;
			}
		};
	}
}
