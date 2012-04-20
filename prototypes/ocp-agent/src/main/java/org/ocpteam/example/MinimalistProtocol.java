package org.ocpteam.example;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import org.ocpteam.component.Protocol;
import org.ocpteam.interfaces.IProtocol;
import org.ocpteam.misc.JLG;

public class MinimalistProtocol extends Protocol implements IProtocol {

	@Override
	public void process(Socket clientSocket) throws Exception {
		DataInputStream in = new DataInputStream(clientSocket.getInputStream());
		DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
		String input = (String) getStreamSerializer().readObject(in);
		JLG.debug("input = " + input);
		JLG.debug("return ok");
		getStreamSerializer().writeObject(out, "ok");
	}

}
