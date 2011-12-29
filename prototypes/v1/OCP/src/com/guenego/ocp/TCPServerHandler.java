package com.guenego.ocp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import com.guenego.misc.JLG;
import com.guenego.misc.TCPServerHandlerInterface;

public class TCPServerHandler implements TCPServerHandlerInterface {

	private Socket clientSocket;
	private Agent agent;

	public TCPServerHandler(Agent agent) {
		this.agent = agent;
	}

	@Override
	public void run() {
		BufferedReader in = null;
		OutputStream out = null;
		try {
			in = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));
			out = clientSocket.getOutputStream();
			String request = in.readLine();
			
			JLG.debug("received(length=" + request.length() + ": " + request);
			String response = (new Protocol(agent)).process(request, clientSocket);
			out.write(response.getBytes());
			// JLG.debug("hash(response)=" + JLG.sha1(response.getBytes()));
			out.flush();

		} catch (Exception e) {
			JLG.error(e);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
				clientSocket.close();
			} catch (Exception e) {
				JLG.error(e);
			}
			JLG.debug("end");
		}
	}

	@Override
	public void setSocket(Socket _clientSocket) {
		clientSocket = _clientSocket;
	}

	@Override
	public TCPServerHandlerInterface duplicate() {
		return new TCPServerHandler(this.agent);
	}
}
