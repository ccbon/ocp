package org.ocpteam.example;

import java.net.Socket;

import org.ocpteam.component.Protocol;
import org.ocpteam.interfaces.IProtocol;
import org.ocpteam.misc.JLG;

public class MinimalistProtocol extends Protocol implements IProtocol {

	@Override
	public byte[] process(byte[] input, Socket clientSocket) throws Exception {
		JLG.debug("return ok");
		return "ok".getBytes();
	}


}
