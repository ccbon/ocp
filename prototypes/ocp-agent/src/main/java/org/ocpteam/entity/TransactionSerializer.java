package org.ocpteam.entity;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

import org.ocpteam.interfaces.ITransaction;
import org.ocpteam.interfaces.ITransactionSerializer;
import org.ocpteam.misc.JLG;

public class TransactionSerializer implements ITransactionSerializer {

	@Override
	public byte[] serialize(Class<? extends ITransaction> transaction,
			Serializable... object) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		ITransaction t = transaction.newInstance();
		dos.writeInt(t.getId());
		for (int i = 0; i < object.length; i++) {
			byte[] serialized = null;

			if (object[i].getClass() == byte[].class) {

				serialized = (byte[]) object[i];
			} else if (object[i].getClass() == String.class) {
				serialized = ((String) object[i]).getBytes();
			} else {
				serialized = JLG.serialize((Serializable) object[i]);
			}
			dos.writeInt(serialized.length);
			dos.write(serialized);
		}
		dos.flush();

		byte[] result = baos.toByteArray();
		dos.close();
		baos.close();
		return result;
	}

	@Override
	public Serializable deserialize(byte[] output) {
		// TODO Auto-generated method stub
		return null;
	}

}
