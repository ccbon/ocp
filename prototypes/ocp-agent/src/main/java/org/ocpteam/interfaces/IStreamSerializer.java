package org.ocpteam.interfaces;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * A Stream serializer allow to extract a complete message from a stream.
 * A message can be read only if it has been written by the same stream serializer.
 * 
 *  Problematic of stream serializer are end of message detection.
 *  It can be done in many ways.
 *
 */
public interface IStreamSerializer {

	byte[] readMessage(DataInputStream in) throws Exception;

	void writeMessage(DataOutputStream out, byte[] message) throws Exception;

}
