package org.ocp.misc;

import java.net.Socket;

public interface TCPServerHandlerInterface extends Runnable {

	void setSocket(Socket clientSocket);

	TCPServerHandlerInterface duplicate();

}
