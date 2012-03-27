package org.ocpteam.module;

import java.io.Serializable;
import java.net.InetAddress;

import org.ocpteam.component.Agent;
import org.ocpteam.component.ContactMap;
import org.ocpteam.component.DSPDataSource;
import org.ocpteam.component.Module;
import org.ocpteam.entity.Contact;
import org.ocpteam.entity.Session;
import org.ocpteam.interfaces.ITransaction;
import org.ocpteam.misc.JLG;

public class DSPModule extends Module {

	protected static final int DECLARE_CONTACT = 1000;
	protected static final int GET_NETWORK_PROPERTIES = 1001;
	protected static final int GET_CONTACT = 1002;
	protected static final int PING = 1003;
	protected static final int DETACH = 1004;

	public ITransaction declareContact() {
		return new ITransaction() {

			@Override
			public Serializable run(Session session, Serializable[] objects)
					throws Exception {
				JLG.debug("declareContact: ");

				Contact contact = (Contact) objects[0];
				InetAddress host = session.getSocket().getInetAddress();
				contact.updateHost(host.getHostAddress());
				session.ds().getComponent(ContactMap.class).add(contact);
				return null;
			}

			@Override
			public int getId() {
				return DECLARE_CONTACT;
			}
		};
	}

	public ITransaction getNetworkProperties() {
		return new ITransaction() {

			@Override
			public Serializable run(Session session, Serializable[] objects)
					throws Exception {
				JLG.debug("get network properties: ");
				DSPDataSource ds = (DSPDataSource) session.ds();
				return ds.network;
			}

			@Override
			public int getId() {
				return GET_NETWORK_PROPERTIES;
			}
		};
	}

	public ITransaction getContact() {
		return new ITransaction() {

			@Override
			public Serializable run(Session session, Serializable[] objects)
					throws Exception {
				JLG.debug("get contact: ");
				Agent agent = session.ds().getComponent(Agent.class);
				return agent.toContact();
			}

			@Override
			public int getId() {
				return GET_CONTACT;
			}
		};
	}

	public ITransaction ping() {
		return new ITransaction() {
			
			@Override
			public Serializable run(Session session, Serializable[] objects)
					throws Exception {
				return null;
			}
			
			@Override
			public int getId() {
				return PING;
			}
		};
	}

	public ITransaction detach() {
		return new ITransaction() {
			
			@Override
			public Serializable run(Session session, Serializable[] objects)
					throws Exception {
				Contact c = (Contact) objects[0];
				ContactMap contactMap = session.ds().getComponent(ContactMap.class);
				contactMap.remove(c.getId());
				return null;
			}
			
			@Override
			public int getId() {
				return DETACH;
			}
		};
	}
}
