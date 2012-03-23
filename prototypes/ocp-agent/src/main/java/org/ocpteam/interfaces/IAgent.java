package org.ocpteam.interfaces;

import org.ocpteam.entity.Contact;


/**
 * An Agent is just an entity that can be client and server.
 * It always has a client and sometimes a server.
 *
 */
public interface IAgent {
	/**
	 * @return the client object used to send request to contact
	 */
	IClient getClient();
	/**
	 * @return the server object or null if it has not.
	 */
	IServer getServer();
	
	
	/**
	 * When an agent starts, it probably wants to connect to an existing agent
	 * (called sponsor agent), except if it is the first one to start.
	 * 
	 * @return true if the agent is the first to start in its environment.
	 * 
	 */
	boolean isFirstAgent();
	
	
	/**
	 * @return a contact containing all the agent public information.
	 */
	public Contact toContact();
}
