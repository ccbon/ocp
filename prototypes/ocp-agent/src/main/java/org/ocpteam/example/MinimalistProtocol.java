package org.ocpteam.example;

import java.net.Socket;

import org.ocpteam.component.Protocol;
import org.ocpteam.interfaces.IProtocol;
import org.ocpteam.misc.JLG;

public class MinimalistProtocol extends Protocol implements IProtocol {

	@Override
	public void process(Socket clientSocket) throws Exception {
		String input = (String) getStreamSerializer().readObject(clientSocket);
		JLG.debug("input = " + input);
		JLG.debug("return ok");
		getStreamSerializer().writeObject(clientSocket, "ok");
	}

}
