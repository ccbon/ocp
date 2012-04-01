package org.ocpteam.interfaces;

import java.net.Socket;

import org.ocpteam.component.TCPServer;

public interface ITCPServerHandler extends Runnable {

	void setSocket(Socket socket);
	
	Socket getSocket();
	
	void setProtocol(IProtocol protocol);

	ITCPServerHandler duplicate();

	void setTCPServer(TCPServer tcpServer);

}
