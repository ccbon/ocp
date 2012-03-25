package org.ocpteam.entity;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import org.ocpteam.interfaces.IStreamSerializer;

public class StreamSerializer implements IStreamSerializer {

	@Override
	public byte[] readMessage(DataInputStream in) throws Exception {
		int length = in.readInt();
		byte[] input = new byte[length];
		in.read(input, 0, length);
		return input;
	}

	@Override
	public void writeMessage(DataOutputStream out, byte[] response)
			throws Exception {
		if (response == null) {
			out.writeInt(0);
			out.flush();
		} else {
			out.writeInt(response.length);
			out.write(response);
			out.flush();
		}
	}

}
