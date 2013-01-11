package org.ocpteam.component;

import java.io.Serializable;
import java.net.InetAddress;

import org.ocpteam.entity.Session;
import org.ocpteam.interfaces.IModule;
import org.ocpteam.interfaces.ITransaction;
import org.ocpteam.misc.LOG;
import org.ocpteam.serializable.Contact;
import org.ocpteam.serializable.ContactList;

public class DSPModule implements IModule {

	protected static final int DECLARE_CONTACT = 1000;
	protected static final int GET_NETWORK_PROPERTIES = 1001;
	protected static final int GET_CONTACT = 1002;
	protected static final int PING = 1003;
	protected static final int DETACH = 1004;
	protected static final int ASK_FOR_CONTACT = 1005;

	public ITransaction declareContact() {
		return new ITransaction() {

			@Override
			public Serializable run(Session session, Serializable[] objects)
					throws Exception {
				LOG.debug("declareContact: ");

				Contact contact = (Contact) objects[0];
				InetAddress host = session.getSocket().getInetAddress();
				contact.setHost(host.getHostAddress());
				session.ds().getComponent(ContactMap.class).add(contact);
				return null;
			}

			@Override
			public int getId() {
				return DECLARE_CONTACT;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "DECLARE_CONTACT";
			}
		};
	}

	public ITransaction getNetworkProperties() {
		return new ITransaction() {

			@Override
			public Serializable run(Session session, Serializable[] objects)
					throws Exception {
				LOG.debug("get network properties: ");
				DSPDataSource ds = (DSPDataSource) session.ds();
				return ds.network;
			}

			@Override
			public int getId() {
				return GET_NETWORK_PROPERTIES;
			}

			@Override
			public String getName() {
				return "GET_NETWORK_PROPERTIES";
			}
		};
	}

	public ITransaction getContact() {
		return new ITransaction() {

			@Override
			public Serializable run(Session session, Serializable[] objects)
					throws Exception {
				LOG.debug("get contact: " + session.ds().toContact());
				return session.ds().toContact();
			}

			@Override
			public int getId() {
				return GET_CONTACT;
			}

			@Override
			public String getName() {
				return "GET_CONTACT";
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

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "PING";
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
				contactMap.remove(c);
				return null;
			}
			
			@Override
			public int getId() {
				return DETACH;
			}

			@Override
			public String getName() {
				return "DETACH";
			}
		};
	}

	public ITransaction askForContact() {
		return new ITransaction() {
			
			@Override
			public Serializable run(Session session, Serializable[] objects)
					throws Exception {
				return new ContactList(session.ds().getComponent(ContactMap.class).getOtherContacts());
			}
			
			@Override
			public int getId() {
				return ASK_FOR_CONTACT;
			}

			@Override
			public String getName() {
				return "ASK_FOR_CONTACT";
			}
		};
	}
}
