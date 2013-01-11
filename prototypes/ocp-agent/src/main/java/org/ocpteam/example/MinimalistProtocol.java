package org.ocpteam.example;

import java.net.Socket;

import org.ocpteam.component.Protocol;
import org.ocpteam.interfaces.IProtocol;
import org.ocpteam.misc.LOG;

public class MinimalistProtocol extends Protocol implements IProtocol {

	@Override
	public void process(Socket clientSocket) throws Exception {
		String input = (String) getStreamSerializer().readObject(clientSocket);
		LOG.debug("input = " + input);
		LOG.debug("return ok");
		getStreamSerializer().writeObject(clientSocket, "ok");
	}

}
