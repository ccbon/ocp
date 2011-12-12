package com.guenego.misc;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TCPClient {

	private String hostname;
	private int port;

	public TCPClient(String hostname, int port) {
		this.hostname = hostname;
		this.port = port;
	}

	public String request(String string) throws Exception {
		String response = new String();
		Socket clientSocket = null;
		PrintWriter out = null;
		BufferedReader in = null;
		try {
			clientSocket = new Socket(hostname, port);
			JLG.debug("socket opened to " + hostname + ":" + port);
			JLG.debug("sending string(length=" + string.length() + ")=" + string);
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			out.print(string);
			out.flush();
			int length = 8192;
			in = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()), length);
			char[] buf = new char[length];
			int readBytes = 0;
			JLG.debug("reading answer");
			while (readBytes != -1) {
				readBytes = in.read(buf);
				if (readBytes >= 0) {
					response += new String(buf, 0, readBytes);
					JLG.debug("readBytes = " + readBytes + " response = ["
							+ response + "]");
				} else if (response.equals("")) {
					readBytes = 0;
				}
			}
			JLG.debug("request=" + string + JLG.NL + "response=" + JLG.sha1(response.getBytes()));
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
		return response;
	}
}
