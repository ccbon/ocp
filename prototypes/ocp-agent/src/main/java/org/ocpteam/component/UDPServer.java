package org.ocpteam.component;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.ocpteam.interfaces.IProtocol;
import org.ocpteam.misc.LOG;

public class UDPServer {

	private int port;
	private DatagramSocket serverSocket;

	private ExecutorService pool;
	private IProtocol protocol;

	public void setPort(int port) {
		this.port = port;
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
						final DatagramPacket receivePacket = new DatagramPacket(
								receiveData, receiveData.length);
						serverSocket.receive(receivePacket);
						pool.execute(new Runnable() {

							@Override
							public void run() {
								try {
									protocol.process(receivePacket);
								} catch (Exception e) {
									e.printStackTrace();
								}
								
							}});
					}
				} catch (Exception e) {
					//e.printStackTrace();
				}
			}
		});

	}

	public void stop() {
		LOG.info("stopping a UDP server with port: " + port);
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
		LOG.info("pool shutdownNow=" + pool);
		LOG.info("end stopping a TCP server with port: " + port);
	}

	public void setProtocol(IProtocol protocol) {
		this.protocol = protocol;
	}

}
