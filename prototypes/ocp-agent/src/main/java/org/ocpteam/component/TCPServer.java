package org.ocpteam.component;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.ocpteam.core.Container;
import org.ocpteam.interfaces.ITCPServerHandler;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.JLGThread;

public class TCPServer extends Container implements Runnable {

	private int port;
	private ServerSocket serverSocket;
	private boolean stoppingNow = false;
	private ITCPServerHandler handler;
	private ThreadGroup tg;
	
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
			tg = new ThreadGroup("tcpserver_" + port);
			while (stoppingNow == false) {
				JLG.debug("waiting for a client connection");
				Socket clientSocket = serverSocket.accept();
				ITCPServerHandler myHandler = handler.duplicate();
				myHandler.setSocket(clientSocket);
				JLGThread t = new JLGThread(tg, myHandler);
				t.start();
			}
		} catch (Exception e) {
			if (!stoppingNow) {
				JLG.error(e);
			}
		}
		JLG.debug("thread finished");
	}

	public void stop(Thread t) {
		JLG.debug("stopping a TCP server with port: " + port);
		stoppingNow = true;
		try {
			if (serverSocket != null) {
				serverSocket.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// interrupt thread of tg.
		Thread[] list = new Thread[tg.activeCount()];
		tg.enumerate(list);
		for (Thread th : list) {
			ITCPServerHandler myHandler = (ITCPServerHandler) ((JLGThread) th).getRunnable();
			Socket socket = myHandler.getSocket();
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		t.interrupt();
		JLG.debug("end stopping a TCP server with port: " + port);
	}

	public void setHandler(ITCPServerHandler handler) {
		this.handler = handler;
	}

}
