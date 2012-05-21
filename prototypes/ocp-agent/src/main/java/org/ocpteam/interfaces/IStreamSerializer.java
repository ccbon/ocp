package org.ocpteam.interfaces;

import java.io.Serializable;
import java.net.Socket;

/**
 * A Stream serializer allow to extract a complete message from a stream.
 * A message can be read only if it has been written by the same stream serializer.
 * 
 *  Problematic of stream serializer are end of message detection.
 *  It can be done in many ways.
 *
 */
public interface IStreamSerializer {

	Serializable readObject(Socket socket) throws Exception;
	
	void writeObject(Socket socket, Serializable o) throws Exception;
	void writeEOM(Socket socket) throws Exception;
	
	byte[] serialize(Serializable o) throws Exception;

}
