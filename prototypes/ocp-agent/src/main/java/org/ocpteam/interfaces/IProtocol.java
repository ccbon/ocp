package org.ocpteam.interfaces;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.DatagramPacket;
import java.net.Socket;

import org.ocpteam.core.IContainer;
import org.ocpteam.entity.StreamSerializer;

public interface IProtocol extends IContainer {

	public StreamSerializer getStreamSerializer();
	
	void process(DatagramPacket packet) throws Exception;

	void process(DataInputStream in, DataOutputStream out, Socket clientSocket)
			throws Exception;

}
