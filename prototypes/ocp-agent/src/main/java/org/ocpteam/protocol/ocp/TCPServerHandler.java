package org.ocpteam.protocol.ocp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import org.ocpteam.component.Agent;
import org.ocpteam.component.StreamSerializer;
import org.ocpteam.interfaces.IStreamSerializer;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.TCPServerHandlerInterface;


public class TCPServerHandler implements TCPServerHandlerInterface {

	private Socket clientSocket;
	private Agent agent;

	public TCPServerHandler(Agent agent) {
		this.agent = agent;
	}

	@Override
	public void run() {
		DataInputStream in = null;
		DataOutputStream out = null;
		try {
			in = new DataInputStream(clientSocket.getInputStream());
			out = new DataOutputStream(clientSocket.getOutputStream());
			IStreamSerializer s = new StreamSerializer();
			byte[] input = s.readMessage(in);
			


			JLG.debug("received length = " + input.length);
			byte[] response = new Protocol(agent).process(input,
					clientSocket);
			s.writeMessage(out, response);
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
