package org.ocpteam.component;

import java.net.Socket;

import org.ocpteam.interfaces.IProtocol;

public class BahBahProtocol extends DataSourceContainer implements IProtocol {

	@Override
	public byte[] process(byte[] input, Socket clientSocket) throws Exception {
		return "bahbah".getBytes();
	}


}
