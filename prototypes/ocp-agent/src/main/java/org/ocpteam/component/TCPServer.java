package org.ocpteam.component;

import java.net.ServerSocket;
import java.net.Socket;

import org.ocpteam.core.Container;
import org.ocpteam.interfaces.ITCPServerHandler;
import org.ocpteam.misc.JLG;

public class TCPServer extends Container implements Runnable {

	private int port;
	private ServerSocket serverSocket;
	private boolean stoppingNow = false;
	private ITCPServerHandler handler;
	
	public TCPServer() throws Exception {
	}
	
	public void setPort(int port) {
		this.port = port;
	}

	public void run() {
		// create a listener
		JLG.debug("starting a TCP server on port:" + port);
		try {
			serverSocket = new ServerSocket(port);
			while (stoppingNow == false) {
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
		JLG.debug("stopping a TCP server");
		stoppingNow = true;
		try {
			if (serverSocket != null) {
				serverSocket.close();
			}
		} catch (Exception e) {
		}
		t.interrupt();
	}

	public void setHandler(ITCPServerHandler handler) {
		this.handler = handler;
	}

}
