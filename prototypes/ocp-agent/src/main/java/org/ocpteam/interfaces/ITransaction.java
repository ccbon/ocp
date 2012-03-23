package org.ocpteam.interfaces;

import java.io.Serializable;
import java.net.Socket;

/**
 * A transaction is a atomic request/response between 2 agents in a distributed network.
 *
 */
public interface ITransaction {
	int getId();
	
	Serializable run(Serializable[] objects);
	
	void setSocket(Socket socket);
}
