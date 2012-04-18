package org.ocpteam.entity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;

import org.ocpteam.interfaces.IStreamSerializer;
import org.ocpteam.misc.JLG;

public class StreamSerializer implements IStreamSerializer {

	@Override
	public Serializable readObject(DataInputStream in) throws Exception {
		int length = in.readInt();
		if (length == -1) {
			return new EOMObject();
		}
		if (length == 0) {
			return null;
		}
		if (length > 1000000) {
			throw new StreamCorruptedException("Message length = " + length + ". Too big object for allocating space.");
		}
		byte[] input = new byte[length];
		in.read(input, 0, length);
		return JLG.deserialize(input);
	}

	@Override
	public void writeObject(DataOutputStream out, Serializable o) throws Exception {
		JLG.debug("start");
		if (o == null) {
			JLG.debug("o is null");
			out.writeInt(0);
		} else {
			JLG.debug("about to serialize");
			byte[] input = serialize(o);
			JLG.debug("about to write the length");
			out.writeInt(input.length);
			out.write(input);
		}
		out.flush();
	}

	@Override
	public void writeEOM(DataOutputStream out) throws Exception {
		out.writeInt(-1);
	}

	@Override
	public byte[] serialize(Serializable o) throws Exception {
		return JLG.serialize(o);
	}

}
