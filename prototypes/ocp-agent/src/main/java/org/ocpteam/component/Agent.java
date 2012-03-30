package org.ocpteam.component;

import java.util.Iterator;

import org.ocpteam.entity.Contact;
import org.ocpteam.interfaces.IAgent;
import org.ocpteam.interfaces.IClient;
import org.ocpteam.interfaces.IListener;
import org.ocpteam.interfaces.IServer;
import org.ocpteam.misc.JLG;

public class Agent extends DataSourceContainer implements IAgent {

	@Override
	public DSPDataSource ds() {
		// TODO Auto-generated method stub
		return (DSPDataSource) super.ds();
	}
	
	@Override
	public IClient getClient() {
		return ds().getComponent(Client.class);
	}

	@Override
	public IServer getServer() {
		return ds().getComponent(Server.class);
	}

	@Override
	public boolean isFirstAgent() {
		return ds().getProperty("agent.isFirst", "yes").equalsIgnoreCase("yes");
	}

	@Override
	public Contact toContact() {
		// convert the agent public information into a contact
		Contact c = new Contact();
		if (ds().getName() == null) {
			ds().setName("ds_" + JLG.random(10000000));
		}
		c.setName(ds().getName());
		// add the listener url and node id information
		if (getServer() != null) {
			Iterator<IListener> it = getServer().getListeners().iterator();
			while (it.hasNext()) {
				IListener l = it.next();
				c.getUrlList().add(l.getUrl());
			}
		}
		return c;
	}

}
