package org.ocpteam.misc;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.net.Socket;
import java.net.SocketException;

public class TCPClient {

	private String hostname;
	private int port;
	private Socket clientSocket;
	private DataInputStream in;
	private DataOutputStream out;

	public TCPClient() {
	}

	public TCPClient(String hostname, int port) {
		this.hostname = hostname;
		this.port = port;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public byte[] request(byte[] input) throws Exception {
		byte[] output = null;

		retrieveSocket();
		try {
			output = request0(input);
		} catch (Exception e) {
			if (e instanceof SocketException || e  instanceof EOFException) {
				createNewSocket();
				output = request0(input);
			}
		}
		return output;
	}

	private byte[] request0(byte[] input) throws Exception {
		byte[] output = null;
		out.writeInt(input.length);
		out.write(input);
		out.flush();
		JLG.debug("input flush");

		int responseLength = in.readInt();
		JLG.debug("response length=" + responseLength);
		output = new byte[responseLength];
		in.read(output, 0, responseLength);
		return output;
	}

	private void retrieveSocket() throws Exception {
		if (clientSocket == null || clientSocket.isClosed()
				|| !clientSocket.isBound() || !clientSocket.isConnected()) {
			createNewSocket();
		}
	}

	private void createNewSocket() throws Exception {
		clientSocket = new Socket(hostname, port);
		try {
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.close();
			}
		} catch (Exception e) {
		}

		in = new DataInputStream(clientSocket.getInputStream());
		out = new DataOutputStream(clientSocket.getOutputStream());

	}

}
