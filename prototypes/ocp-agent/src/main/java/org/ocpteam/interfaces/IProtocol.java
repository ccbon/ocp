package org.ocpteam.interfaces;

import java.net.DatagramPacket;
import java.net.Socket;

import org.ocpteam.core.IContainer;

public interface IProtocol extends IContainer {

	public IStreamSerializer getStreamSerializer();
	
	void process(DatagramPacket packet) throws Exception;

	void process(Socket clientSocket) throws Exception;

}
