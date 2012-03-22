package org.ocpteam.component;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import org.ocpteam.interfaces.IStreamSerializer;

public class StreamSerializer extends DataSourceContainer implements IStreamSerializer {

	@Override
	public byte[] readMessage(DataInputStream in) throws Exception {
		int length = in.readInt();
		byte[] input = new byte[length];
		in.read(input, 0, length);
		return input;
	}

	@Override
	public void writeMessage(DataOutputStream out, byte[] response) throws Exception {
		out.writeInt(response.length);
		out.write(response);
		out.flush();
	}

}
