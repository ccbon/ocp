package org.ocpteam.transaction;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;

import org.ocpteam.component.ContactMap;
import org.ocpteam.entity.Contact;

public class DeclareContactTransaction extends Transaction {

	private Socket clientSocket;

	@Override
	public int getId() {
		return 0;
	}

	@Override
	public Serializable run(Serializable[] objects) {
		Contact c = (Contact) objects[0];
		InetAddress host = clientSocket.getInetAddress();
		c.updateHost(host.getHostAddress());
		ds().getComponent(ContactMap.class).add(c);
		return null;
	}

	@Override
	public void setSocket(Socket socket) {
		clientSocket = socket;		
	}
	

}
