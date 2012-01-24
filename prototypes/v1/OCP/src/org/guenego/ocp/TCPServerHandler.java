package org.guenego.ocp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import org.guenego.misc.JLG;
import org.guenego.misc.TCPServerHandlerInterface;


public class TCPServerHandler implements TCPServerHandlerInterface {

	private Socket clientSocket;
	private OCPAgent agent;

	public TCPServerHandler(OCPAgent agent) {
		this.agent = agent;
	}

	@Override
	public void run() {
		DataInputStream in = null;
		DataOutputStream out = null;
		try {
			in = new DataInputStream(clientSocket.getInputStream());
			out = new DataOutputStream(clientSocket.getOutputStream());
			int length = in.readInt();
			JLG.debug("length=" + length);
			byte[] input = new byte[length];
			in.read(input, 0, length);


			JLG.debug("received length = " + length);
			byte[] response = new Protocol(agent).process(input,
					clientSocket);
			out.writeInt(response.length);
			out.write(response);
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
