package org.ocpteam.component;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.ocpteam.core.Container;
import org.ocpteam.interfaces.IUDPServerHandler;
import org.ocpteam.misc.JLG;

public class UDPServer extends Container {

	private int port;
	private DatagramSocket serverSocket;
	private UDPServerHandler handler;

	private ExecutorService pool;

	@Override
	public void init() throws Exception {
		super.init();
	}

	public void setPort(int port) {
		this.port = port;

	}

	public void setHandler(UDPServerHandler handler) {
		this.handler = handler;

	}

	public void start() {
		pool = Executors.newCachedThreadPool();
		pool.execute(new Runnable() {

			@Override
			public void run() {
				try {
					serverSocket = new DatagramSocket(port);
					while (true) {
						byte[] receiveData = new byte[8192];
						DatagramPacket receivePacket = new DatagramPacket(
								receiveData, receiveData.length);
						serverSocket.receive(receivePacket);
						IUDPServerHandler myHandler = handler.duplicate();
						myHandler.setDatagramPacket(receivePacket);
						pool.execute(myHandler);
					}
				} catch (Exception e) {
					//e.printStackTrace();
				}
			}
		});

	}

	public void stop() {
		JLG.debug("stopping a UDP server with port: " + port);
		try {
			if (serverSocket != null) {
				serverSocket.close();
				serverSocket = null;
			}
		} catch (Exception e) {
		}
		
		// make sure we don't accept new tasks
		pool.shutdown();
		pool.shutdownNow();
		JLG.debug("pool shutdownNow=" + pool);
		JLG.debug("end stopping a TCP server with port: " + port);
	}

}
