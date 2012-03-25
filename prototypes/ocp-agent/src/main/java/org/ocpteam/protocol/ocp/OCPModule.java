package org.ocpteam.protocol.ocp;

import java.io.Serializable;

import org.ocpteam.component.Agent;
import org.ocpteam.entity.Session;
import org.ocpteam.interfaces.ITransaction;
import org.ocpteam.misc.JLG;
import org.ocpteam.module.DSPModule;

public class OCPModule extends DSPModule {
	
	protected static final int REQUEST_NODE_ID = 2001;

	@Override
	public ITransaction getNetworkProperties() {
		return new ITransaction() {

			@Override
			public Serializable run(Session session, Serializable[] objects)
					throws Exception {
				JLG.debug("get ocp network properties: ");
				OCPDataSource ds = (OCPDataSource) session.ds();
				return ds.network;
			}

			@Override
			public int getId() {
				return GET_NETWORK_PROPERTIES;
			}
		};
	}
	
	public ITransaction requestNodeId() {
		return new ITransaction() {
			
			@Override
			public Serializable run(Session session, Serializable[] objects)
					throws Exception {
				OCPAgent agent = (OCPAgent) session.ds().getComponent(Agent.class);
				return agent.generateId();
			}
			
			@Override
			public int getId() {
				return REQUEST_NODE_ID;
			}
		};
	}
}
