package org.ocpteam.module;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.Properties;

import org.ocpteam.component.Agent;
import org.ocpteam.component.ContactMap;
import org.ocpteam.component.Module;
import org.ocpteam.entity.Contact;
import org.ocpteam.entity.Session;
import org.ocpteam.interfaces.ITransaction;
import org.ocpteam.misc.JLG;

public class DSPModule extends Module {

	protected static final int DECLARE_CONTACT = 1000;
	protected static final int GET_NETWORK_PROPERTIES = 1001;
	protected static final int GET_CONTACT = 1002;
	
	
	public ITransaction declareContact = new ITransaction() {

		@Override
		public Serializable run(Session session, Serializable[] objects) throws Exception {
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


	public ITransaction getNetworkProperties = new ITransaction() {
		
		@Override
		public Serializable run(Session session, Serializable[] objects)
				throws Exception {
			Properties p = new Properties();
			p.setProperty("hello", "world");
			return p;
		}
		
		@Override
		public int getId() {
			return GET_NETWORK_PROPERTIES;
		}
	};
	public ITransaction getContact = new ITransaction() {
		
		@Override
		public Serializable run(Session session, Serializable[] objects)
				throws Exception {
			Agent agent = session.ds().getComponent(Agent.class);
			return agent.toContact();
		}
		
		@Override
		public int getId() {
			return GET_CONTACT;
		}
	};

}