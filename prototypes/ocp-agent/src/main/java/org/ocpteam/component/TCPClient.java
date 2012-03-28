package org.ocpteam.component;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.net.Socket;
import java.net.SocketException;

import org.ocpteam.interfaces.IProtocol;
import org.ocpteam.interfaces.IStreamSerializer;
import org.ocpteam.misc.JLG;

public class TCPClient extends DataSourceContainer {

	private String hostname;
	private int port;
	private Socket clientSocket;
	private DataInputStream in;
	private DataOutputStream out;
	private IProtocol protocol;

	public TCPClient() {
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
		IStreamSerializer s = protocol.getStreamSerializer();
		s.writeMessage(out, input);
		JLG.debug("input flush");
		
		output = s.readMessage(in);
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

	public IProtocol getProtocol() {
		return protocol;
	}

	public void setProtocol(IProtocol protocol) {
		this.protocol = protocol;
	}

}
