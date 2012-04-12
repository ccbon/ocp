package org.ocpteam.component;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.ocpteam.core.Container;
import org.ocpteam.interfaces.IProtocol;
import org.ocpteam.misc.JLG;

public class UDPServer extends Container {

	private int port;
	private DatagramSocket serverSocket;

	private ExecutorService pool;
	private IProtocol protocol;

	@Override
	public void init() throws Exception {
		super.init();
	}

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

	public void setProtocol(IProtocol protocol) {
		this.protocol = protocol;
	}

}
