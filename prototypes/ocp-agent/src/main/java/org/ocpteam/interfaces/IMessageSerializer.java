package org.ocpteam.interfaces;

import java.io.Serializable;

import org.ocpteam.entity.InputMessage;

/**
 * A message serializer is used to (de)serialize a transacion input/output.
 *
 */
public interface IMessageSerializer {
	byte[] serializeInput(InputMessage inputMessage) throws Exception;
	InputMessage deserializeInput(byte[] input) throws Exception;
	
	byte[] serializeOutput(Serializable s) throws Exception;
	Serializable deserializeOutput(byte[] output) throws Exception;
	
}
