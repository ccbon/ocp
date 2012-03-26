package org.ocpteam.misc;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class TCPClient {

	private String hostname;
	private int port;

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
		Socket clientSocket = null;
		DataOutputStream out = null;
		DataInputStream in = null;
		try {
			clientSocket = new Socket(hostname, port);
			JLG.debug("socket opened to " + hostname + ":" + port);
			JLG.debug("sending string(length=" + input.length + ")");
			JLG.debug("sending string: " + input);
			out = new DataOutputStream(clientSocket.getOutputStream());
			out.writeInt(input.length);
			out.write(input);
			out.flush();
			
			in = new DataInputStream(clientSocket.getInputStream());
			int responseLength = in.readInt();
			JLG.debug("response length=" + responseLength);
			output = new byte[responseLength];
			in.read(output, 0, responseLength);
			JLG.debug("response length=" + responseLength);
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
				if (clientSocket != null) {
					clientSocket.close();
				}
			} catch (Exception e) {
			}
		}
		return output;
	}

}
