package org.ocpteamx.protocol.ocp;

import java.util.Iterator;

import org.ocpteam.component.Client;
import org.ocpteam.component.ContactMap;
import org.ocpteam.component.DSPDataSource;
import org.ocpteam.component.PersistentFileMap;
import org.ocpteam.component.Protocol;
import org.ocpteam.component.Server;
import org.ocpteam.component.UserManagement;
import org.ocpteam.interfaces.IAuthenticable;
import org.ocpteam.interfaces.IDataStore;
import org.ocpteam.interfaces.IUserCreation;
import org.ocpteam.interfaces.IUserManagement;
import org.ocpteam.misc.Id;
import org.ocpteam.misc.LOG;
import org.ocpteam.serializable.Contact;

public class OCPDataSource extends DSPDataSource {

	protected OCPAgent agent;
	public IUserManagement um;

	public OCPDataSource() throws Exception {
		super();

		replaceComponent(Client.class, new OCPClient());
		replaceComponent(Server.class, new OCPServer());
		replaceComponent(Protocol.class, new OCPProtocol());
		replaceComponent(ContactMap.class, new OCPContactMap());

		addComponent(OCPAgent.class);
		addComponent(IDataStore.class, new PersistentFileMap());
		addComponent(IUserManagement.class, new UserManagement());
		addComponent(IUserCreation.class, new OCPUserCreation());
		addComponent(OCPModule.class);
	}

	@Override
	public void init() throws Exception {
		super.init();
		agent = getComponent(OCPAgent.class);
		um = getComponent(IUserManagement.class);
		contactClass = OCPContact.class;
		addComponent(IAuthenticable.class, (IAuthenticable) client);

	}

	@Override
	public String getProtocolName() {
		return "OLD_OCP";
	}

	@Override
	public void connect() throws Exception {
		agent.readConfig();
		super.connect();
	}

	@Override
	protected void readNetworkConfig() throws Exception {
		super.readNetworkConfig();
		agent.connect();
	}

	@Override
	public void disconnect() throws Exception {
		agent.disconnect();
	}

	@Override
	public void configureServer() throws Exception {
	}

	@Override
	public String getName() {
		if (agent.id != null) {
			return agent.id.toString();
		}
		return super.getName();
	}

	@Override
	public Contact toContact() throws Exception {
		// convert the agent public information into a contact
		OCPContact c = (OCPContact) super.toContact();
		setName(agent.id.toString());
		c.publicKey = agent.keyPair.getPublic().getEncoded();
		// add the listener url and node id information
		if (agent.storage != null) {
			Iterator<Id> itn = agent.storage.nodeSet.iterator();
			while (itn.hasNext()) {
				Id nodeId = itn.next();
				c.nodeIdSet.add(nodeId);
			}
		}
		LOG.info("toContact: " + c);
		return c;
	}

	public OCPAgent getOCPAgent() {
		return getComponent(OCPAgent.class);
	}

}
