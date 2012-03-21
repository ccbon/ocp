package org.ocpteam.component;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import org.ocpteam.core.Component;
import org.ocpteam.interfaces.IProtocol;
import org.ocpteam.interfaces.IStreamSerializer;
import org.ocpteam.interfaces.ITCPServerHandler;
import org.ocpteam.misc.JLG;


public class TCPServerHandler extends Component implements ITCPServerHandler {

	private Socket clientSocket;

	@Override
	public void run() {
		DataInputStream in = null;
		DataOutputStream out = null;
		try {
			in = new DataInputStream(clientSocket.getInputStream());
			out = new DataOutputStream(clientSocket.getOutputStream());
			IStreamSerializer s = parent.getDesigner().get(StreamSerializer.class);
			byte[] input = s.readMessage(in);
			JLG.debug("received length = " + input.length);
			JLG.debug("parent: " + parent);
			byte[] response = parent.getDesigner().get(IProtocol.class).process(input,
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
	public ITCPServerHandler duplicate() {
		TCPServerHandler handler = new TCPServerHandler();
		handler.setParent(parent);
		return handler;
	}
}
