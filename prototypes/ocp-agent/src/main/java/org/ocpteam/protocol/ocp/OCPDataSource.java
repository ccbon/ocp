package org.ocpteam.protocol.ocp;

import java.util.Iterator;

import org.ocpteam.component.Agent;
import org.ocpteam.component.Authentication;
import org.ocpteam.component.Client;
import org.ocpteam.component.ContactMap;
import org.ocpteam.component.DSPDataSource;
import org.ocpteam.component.PersistentFileMap;
import org.ocpteam.component.Protocol;
import org.ocpteam.component.Server;
import org.ocpteam.component.UserCreation;
import org.ocpteam.component.UserIdentification;
import org.ocpteam.entity.Contact;
import org.ocpteam.interfaces.IAuthenticable;
import org.ocpteam.interfaces.IPersistentMap;
import org.ocpteam.misc.Id;
import org.ocpteam.misc.JLG;

public class OCPDataSource extends DSPDataSource {

	protected OCPAgent agent;
	public Authentication authentication;

	public OCPDataSource() throws Exception {
		super();
		replaceComponent(Agent.class, new OCPAgent());
		replaceComponent(Client.class, new OCPClient());
		replaceComponent(Server.class, new OCPServer());
		replaceComponent(Protocol.class, new OCPProtocol());
		replaceComponent(ContactMap.class, new OCPContactMap());

		addComponent(UserIdentification.class, new Authentication());
		addComponent(UserCreation.class);
		addComponent(IPersistentMap.class, new PersistentFileMap());
		addComponent(OCPModule.class);
	}

	@Override
	public void init() throws Exception {
		super.init();
		agent = (OCPAgent) getComponent(Agent.class);
		authentication = (Authentication) getComponent(UserIdentification.class);
		contactClass = OCPContact.class;
		addComponent(IAuthenticable.class, (IAuthenticable) client);
		
	}

	@Override
	public String getProtocolName() {
		return "OCP";
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
		((OCPAgent) getComponent(Agent.class)).disconnect();
	}

	@Override
	protected void configureServer(Server server) throws Exception {
	}

	@Override
	public String getName() {
		if (((OCPAgent) getComponent(Agent.class)).id != null) {
			return ((OCPAgent) getComponent(Agent.class)).id.toString();
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
		JLG.debug("toContact: " + c);
		return c;
	}

}
