package org.ocpteam.protocol.ocp;

import java.io.Serializable;

import org.ocpteam.component.Agent;
import org.ocpteam.component.DSPModule;
import org.ocpteam.entity.Session;
import org.ocpteam.interfaces.ITransaction;

public class OCPModule extends DSPModule {
	
	protected static final int REQUEST_NODE_ID = 2001;
	
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

			@Override
			public String getName() {
				return "REQUEST_NODE_ID";
			}
		};
	}
}
