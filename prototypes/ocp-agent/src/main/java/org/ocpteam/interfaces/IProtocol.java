package org.ocpteam.interfaces;

import java.net.Socket;

public interface IProtocol {

	byte[] process(byte[] input, Socket clientSocket) throws Exception;

	IStreamSerializer getStreamSerializer();
	ITransactionSerializer getTransactionSerializer();

}
