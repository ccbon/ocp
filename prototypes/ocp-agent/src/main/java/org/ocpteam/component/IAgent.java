package org.ocpteam.component;

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
}
