package org.ocpteam.interfaces;

import java.net.Socket;

import org.ocpteam.core.IContainer;

public interface IProtocol extends IContainer {

	byte[] process(byte[] input, Socket clientSocket) throws Exception;

	IStreamSerializer getStreamSerializer();
	IMessageSerializer getMessageSerializer();

}
