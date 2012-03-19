package org.ocpteam.interfaces;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public interface IStreamSerializer {

	byte[] readMessage(DataInputStream in) throws Exception;

	void writeMessage(DataOutputStream out, byte[] response) throws Exception;

}
