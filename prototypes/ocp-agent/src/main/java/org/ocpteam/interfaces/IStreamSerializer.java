package org.ocpteam.interfaces;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * A Stream serializer allow to extract a complete message from a stream.
 * A message can be read only if it has been written by the same stream serializer.
 * 
 *  Problematic of stream serializer are end of message detection.
 *  It can be done in many ways.
 *
 */
public interface IStreamSerializer {

	Serializable readObject(DataInputStream in) throws Exception;
	
	void writeObject(DataOutputStream out, Serializable o) throws Exception;
	void writeEOM(DataOutputStream out) throws Exception;
	
	byte[] serialize(Serializable o) throws Exception;

}
