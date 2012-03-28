package org.ocpteam.component;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.net.SocketException;

import org.ocpteam.core.Component;
import org.ocpteam.interfaces.IProtocol;
import org.ocpteam.interfaces.IStreamSerializer;
import org.ocpteam.interfaces.ITCPServerHandler;
import org.ocpteam.misc.JLG;

public class TCPServerHandler extends Component implements ITCPServerHandler {

	private Socket clientSocket;
	private IProtocol protocol;

	@Override
	public void run() {
		DataInputStream in = null;
		DataOutputStream out = null;
		try {
			in = new DataInputStream(clientSocket.getInputStream());
			out = new DataOutputStream(clientSocket.getOutputStream());
			IStreamSerializer s = protocol.getStreamSerializer();
			int i = 0;
			while (i < 1000) {
				JLG.debug("wait for message");
				byte[] input = s.readMessage(in);
				JLG.debug("received length = " + input.length);
				byte[] response = protocol.process(input, clientSocket);
				s.writeMessage(out, response);
				i++;
			}
		} catch (SocketException e) {
		} catch (Exception e) {
			e.printStackTrace();
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
	public ITCPServerHandler duplicate() {
		TCPServerHandler handler = new TCPServerHandler();
		handler.setParent(parent);
		handler.setProtocol(protocol);
		return handler;
	}

	@Override
	public void setProtocol(IProtocol protocol) {
		this.protocol = protocol;
	}

	@Override
	public Socket getSocket() {
		return clientSocket;
	}
}
