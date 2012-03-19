package org.ocpteam.interfaces;

import java.net.Socket;

public interface ITCPServerHandler extends Runnable {

	void setSocket(Socket clientSocket);

	ITCPServerHandler duplicate();

}
