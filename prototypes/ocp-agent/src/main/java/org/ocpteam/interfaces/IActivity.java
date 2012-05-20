package org.ocpteam.interfaces;

import java.io.Serializable;
import java.net.Socket;

import org.ocpteam.component.Protocol;
import org.ocpteam.entity.Session;

public interface IActivity {

	void run(Session session, Serializable[] objects,
			Socket socket, Protocol protocol) throws Exception;
	
	int getId();

}
