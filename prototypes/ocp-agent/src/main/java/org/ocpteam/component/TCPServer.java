package org.ocpteam.component;

import java.net.ServerSocket;
import java.net.Socket;

import org.ocpteam.core.Component;
import org.ocpteam.interfaces.ITCPServerHandler;
import org.ocpteam.misc.JLG;

public class TCPServer extends Component implements Runnable {

	private int port;
	private ITCPServerHandler handler;
	private ServerSocket serverSocket;
	private boolean stoppingNow;
	
	public void setPort(int port) {
		this.port = port;
	}
	
	public void setHandler(ITCPServerHandler handler) {
		this.handler = handler;
	}

	public void run() {
		// create a listener
		JLG.debug("starting a TCP server");
		try {
			serverSocket = new ServerSocket(port);
			while (true) {
				JLG.debug("waiting for a client connection");
				Socket clientSocket = serverSocket.accept();
				ITCPServerHandler myHandler = handler.duplicate();
				myHandler.setSocket(clientSocket);
				new Thread(myHandler).start();
			}
		} catch (Exception e) {
			if (!stoppingNow) {
				JLG.error(e);
			}
		}
	}

	public void stop(Thread t) {
		stoppingNow = true;
		try {
			if (serverSocket != null) {
				serverSocket.close();
			}
		} catch (Exception e) {

		}
		t.interrupt();
	}

}
