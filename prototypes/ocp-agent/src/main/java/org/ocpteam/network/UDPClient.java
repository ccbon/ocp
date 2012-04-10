package org.ocpteam.network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPClient {

	private String host;
	private int port;

	public UDPClient(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public void send(byte[] message) throws Exception {
		DatagramSocket clientSocket = new DatagramSocket();
		InetAddress ipAddress = InetAddress.getByName(host);
		DatagramPacket sendPacket = new DatagramPacket(message,
				message.length, ipAddress, port);
		clientSocket.send(sendPacket);
		clientSocket.close();

	}

}
