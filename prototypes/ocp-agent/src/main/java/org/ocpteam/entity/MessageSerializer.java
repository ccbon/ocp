package org.ocpteam.entity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

import org.ocpteam.component.Protocol;
import org.ocpteam.interfaces.IMessageSerializer;
import org.ocpteam.interfaces.ITransaction;
import org.ocpteam.misc.JLG;

public class MessageSerializer implements IMessageSerializer {

	private Protocol protocol;

	public MessageSerializer(Protocol protocol) {
		this.protocol = protocol;
	}

	@Override
	public byte[] serializeInput(InputMessage inputMessage) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		dos.writeInt(inputMessage.transaction.getId());
		byte[]serialized = JLG.serialize((Serializable) inputMessage.objects);
		dos.writeInt(serialized.length);
		dos.write(serialized);
		dos.flush();

		byte[] result = baos.toByteArray();
		dos.close();
		baos.close();
		return result;
	}

	@Override
	public InputMessage deserializeInput(byte[] input) throws Exception {
		ByteArrayInputStream bais = new ByteArrayInputStream(input);
		DataInputStream dis = new DataInputStream(bais);
		int transid = dis.readInt();
		ITransaction trans = protocol.getMap().get(transid);
		int length = dis.readInt();
		byte[] serialized = new byte[length];
		dis.read(serialized, 0, length);
		Serializable[] objects = (Serializable[]) JLG.deserialize(serialized);
		dis.close();
		bais.close();
		return new InputMessage(trans, objects);
	}
	
	@Override
	public byte[] serializeOutput(Serializable s) throws Exception {
		return JLG.serialize(s);
	}

	@Override
	public Serializable deserializeOutput(byte[] output) throws Exception {
		return JLG.deserialize(output);
	}

}
