package org.ocpteam.interfaces;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import org.ocpteam.core.IComponent;

public interface IStreamSerializer extends IComponent {

	byte[] readMessage(DataInputStream in) throws Exception;

	void writeMessage(DataOutputStream out, byte[] response) throws Exception;

}
