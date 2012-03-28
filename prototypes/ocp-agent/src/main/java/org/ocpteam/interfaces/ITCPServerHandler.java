package org.ocpteam.interfaces;

import java.net.Socket;

public interface ITCPServerHandler extends Runnable {

	void setSocket(Socket socket);
	
	void setProtocol(IProtocol protocol);

	ITCPServerHandler duplicate();

	Socket getSocket();

}
