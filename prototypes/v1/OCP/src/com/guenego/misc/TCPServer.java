package com.guenego.misc;

import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer extends Thread {

	private int port;
	private TCPServerHandlerInterface handler;
	private ServerSocket serverSocket;
	private boolean stoppingNow;

	public TCPServer(int _port, TCPServerHandlerInterface _handler) {
		// TODO Auto-generated constructor stub
		port = _port;
		handler = _handler;
	}

	public void run() {
		// create a listener
		JLG.debug("starting a TCP server");
		try {
			serverSocket = new ServerSocket(port);
			while (true) {
				JLG.debug("waiting for a client connection");
				Socket clientSocket = serverSocket.accept();
				TCPServerHandlerInterface myHandler = handler.duplicate();
				myHandler.setSocket(clientSocket);
				new Thread(myHandler).start();
			}
		} catch (Exception e) {
			if (!stoppingNow) {
				JLG.error(e);
			}
		}
	}

	public void stopnow() {
		stoppingNow = true;
		try {
			if (serverSocket != null) {
				serverSocket.close();
			}
		} catch (Exception e) {

		}
		this.interrupt();
	}

}
